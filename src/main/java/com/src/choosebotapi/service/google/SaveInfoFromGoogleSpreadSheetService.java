package com.src.choosebotapi.service.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.src.choosebotapi.data.model.google.GoogleSpreadSheet;
import com.src.choosebotapi.data.model.restaurant.Dish;
import com.src.choosebotapi.data.model.restaurant.DishCategory;
import com.src.choosebotapi.data.model.restaurant.DishKitchenDirection;
import com.src.choosebotapi.data.model.restaurant.Restaurant;
import com.src.choosebotapi.data.repository.google.GoogleSpreadSheetRepository;
import com.src.choosebotapi.data.repository.restaurant.DishCategoryRepository;
import com.src.choosebotapi.data.repository.restaurant.DishKitchenDirectionRepository;
import com.src.choosebotapi.data.repository.restaurant.DishRepository;
import com.src.choosebotapi.data.repository.restaurant.RestaurantRepository;
import lombok.AccessLevel;
import lombok.Synchronized;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
@EnableScheduling
@EnableAsync
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource("classpath:google.properties")
public class SaveInfoFromGoogleSpreadSheetService {

    @Autowired
    DishCategoryRepository dishCategoryRepository;

    @Autowired
    DishKitchenDirectionRepository dishKitchenDirectionRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    DishRepository dishRepository;

    @Autowired
    GoogleSpreadSheetRepository googleSpreadSheetRepository;

    private static final String APPLICATION_NAME = "Google Drive API Java Choose bot application";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = SaveInfoFromGoogleSpreadSheetService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    //        @Scheduled(fixedDelay = 5000)
    @Scheduled(cron = "00 */5  * * * *")
    @Async
    @Synchronized
    public void logInAndLoadFiles() throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        saveInfoFromGoogleSpreadSheet(service);
    }


    public void saveInfoFromGoogleSpreadSheet(Drive service) {

        ArrayList<GoogleSpreadSheet> googleSpreadSheetsRows = (ArrayList<GoogleSpreadSheet>) googleSpreadSheetRepository.findAll();

        for (GoogleSpreadSheet row : googleSpreadSheetsRows) {
            Optional<Dish> dishOptional = dishRepository.getDishByGoogleSpreadSheetRow_Id(row.getId());
            boolean dishExisted = dishOptional.isPresent();

            if (checkDish(row)) {
                DishCategory dishCategoryEntity = saveDishCategory(row.getDishCategory());
                DishKitchenDirection dishKitchenDirectionEntity = saveDishKitchenDirection(row.getDishKitchen());
                Restaurant restaurant = saveRestaurant(row.getRestaurantName(), row.getRestaurantAddress(), row.getAverageCheck());

                byte[] photoBytes = null;
                try {
                    String photoId = "";
                    try {
                        photoId = getPhotoId(row.getDishPhotoUrl());
                    } catch (Exception e) {
                        log.error("Ошибка при получении id изображения блюда. Строка " + row.getRowIndex() + ". Описание ошибки: " + e.getMessage());
                    }

                    if (!photoId.equals("")) {
                        log.info("downloading image for row " + row.getRowIndex());
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        service.files().get(photoId).executeMediaAndDownloadTo(outputStream);
                        photoBytes = outputStream.toByteArray();
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }

                if (photoBytes != null) {
                    if (!dishExisted) {
                        Dish dishDB = new Dish();
                        saveUpdateDish(dishDB, row.getDishName(), row.getDishDescription(), row.getDishPrice(), dishCategoryEntity, dishKitchenDirectionEntity,
                                restaurant, photoBytes, row);
                    } else {
                        saveUpdateDish(dishOptional.get(), row.getDishName(), row.getDishDescription(), row.getDishPrice(),
                                dishCategoryEntity, dishKitchenDirectionEntity, restaurant, photoBytes, row);
                    }
                } else {
                    log.warn("Блюдо " + row.getDishName() + "не было сохранено по причине: Нет фото. Строка: " + row.getRowIndex());
                }
            }
        }
    }

    private boolean checkDish(GoogleSpreadSheet row) {
        Optional<Dish> dishOptional = dishRepository.getDishByGoogleSpreadSheetRow_Id(row.getId());

        if (dishOptional.isEmpty()) {
            return true;
        } else {
            String logMessage = "Различие в строке " + row.getRowIndex() + ". Причина: ";

            Dish dish = dishOptional.get();
            if (!row.getDishName().equals(dish.getName())) {
                log.warn(logMessage + "разные имена блюд. Гугл таблица: " + row.getDishName() + ", БД: " + dish.getName());
                return true;
            }

            if (!row.getDishCategory().equals(dish.getCategory().getName())) {
                log.warn(logMessage + "разные имена категорий. Гугл таблица: " + row.getDishCategory() + ", БД: " + dish.getCategory().getName());
                return true;
            }

            if (!row.getAverageCheck().equals(dish.getRestaurant().getAverageCheck())) {
                log.warn(logMessage + "разные средние чеки ресторанов блюд. Гугл таблица: " + row.getAverageCheck() + ", БД: " + dish.getRestaurant().getAverageCheck());
                return true;
            }

            if (!row.getDishPrice().equals(dish.getPrice())) {
                log.warn(logMessage + "разные стоимости блюд. Гугл таблица: " + row.getDishPrice() + ", БД: " + dish.getPrice());
                return true;
            }

            if (!row.getDishDescription().equals(dish.getDescription())) {
                log.warn(logMessage + "разные описания блюд. Гугл таблица: " + row.getDishDescription() + ", БД: " + dish.getDescription());
                return true;
            }

            if (!row.getDishKitchen().equals(dish.getKitchenDirection().getName())) {
                log.warn(logMessage + "разные кухни блюд. Гугл таблица: " + row.getDishKitchen() + ", БД: " + dish.getKitchenDirection().getName());
                return true;
            }

            if (!row.getRestaurantAddress().equals(dish.getRestaurant().getAddress())) {
                log.warn(logMessage + "разные адреса ресторанов. Гугл таблица: " + row.getRestaurantAddress() + ", БД: " + dish.getRestaurant().getAddress());
                return true;
            }

            if (!row.getRestaurantName().equals(dish.getRestaurant().getName())) {
                log.warn(logMessage + "разные имена ресторанов. Гугл таблица: " + row.getRestaurantName() + ", БД: " + dish.getRestaurant().getName());
                return true;
            }

            if (dish.getImage() == null) {
                log.warn(logMessage + "нет фото");
                return true;
            }

            return false;
        }
    }

    void saveUpdateDish(Dish dish, String dishName, String dishDescription, Float dishPrice, DishCategory dishCategoryEntity,
                        DishKitchenDirection dishKitchenDirectionEntity, Restaurant restaurant, byte[] photoBytes, GoogleSpreadSheet googleSpreadSheet) {
        dish.setName(dishName);
        dish.setDescription(dishDescription);
        dish.setPrice(dishPrice);
        dish.setCategory(dishCategoryEntity);
        dish.setKitchenDirection(dishKitchenDirectionEntity);
        dish.setRestaurant(restaurant);
        dish.setImage(photoBytes);
        dish.setGoogleSpreadSheetRow(googleSpreadSheet);
        dishRepository.save(dish);
    }

    Restaurant saveRestaurant(String restaurantName, String restaurantAddress, String averageCheck) {
        return restaurantRepository.findByNameAndAddress(restaurantName, restaurantAddress)
                .orElseGet(() -> {
                    Restaurant restaurantDB = new Restaurant();
                    restaurantDB.setName(restaurantName);
                    restaurantDB.setAddress(restaurantAddress);
                    restaurantDB.setAverageCheck(averageCheck);
                    HashMap<String, Float> coordinates = restaurantDB.getCoordinatesFromYandex();
                    restaurantDB.setLatitude(coordinates.get("latitude"));
                    restaurantDB.setLongitude(coordinates.get("longitude"));

                    return restaurantRepository.findByNameAndLongitudeAndLatitude(restaurantName, coordinates.get("longitude")
                            , coordinates.get("latitude")).orElseGet(() -> restaurantRepository.save(restaurantDB));
                });
    }

    DishKitchenDirection saveDishKitchenDirection(String dishKitchenDirection) {
        DishKitchenDirection dishKitchenDirectionEntity = null;
        if (!dishKitchenDirection.equals("")) {
            dishKitchenDirectionEntity = dishKitchenDirectionRepository.findByName(dishKitchenDirection)
                    .orElseGet(() -> {
                        DishKitchenDirection dishKitchenDirectionDB = new DishKitchenDirection();
                        dishKitchenDirectionDB.setName(dishKitchenDirection);
                        return dishKitchenDirectionRepository.save(dishKitchenDirectionDB);
                    });
        }
        return dishKitchenDirectionEntity;
    }

    DishCategory saveDishCategory(String dishCategory) {
        DishCategory dishCategoryEntity = null;
        if (!dishCategory.equals("")) {
            dishCategoryEntity = dishCategoryRepository.findByName(dishCategory)
                    .orElseGet(() -> {
                        DishCategory dishCategoryDB = new DishCategory();
                        dishCategoryDB.setName(dishCategory);
                        return dishCategoryRepository.save(dishCategoryDB);
                    });
        }
        return dishCategoryEntity;
    }

    private String getPhotoId(String url) {
        Pattern pattern = Pattern.compile("[-\\w]{25,}");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return url.substring(matcher.start(), matcher.end());
        }
        return "";
    }

}
