package com.src.choosebotapi.service.google;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.src.choosebotapi.data.model.*;
import com.src.choosebotapi.data.repository.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@EnableScheduling
@EnableAsync
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource("classpath:google.properties")
public class GoogleSpreadSheetUpdateService {

    final long updatePeriod = 5000;
    final String linkMatcher = "?id=";

    @Autowired
    GoogleSpreadSheetRepository googleSpreadSheetRepository;

    @Autowired
    DishCategoryRepository dishCategoryRepository;

    @Autowired
    DishKitchenDirectionRepository dishKitchenDirectionRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    DishRepository dishRepository;

    @Getter
    @Value("${google.key}")
    String key;

    @Getter
    @Value("${google.spreadsheetId}")
    String spreadsheetId;

    @Getter
    @Value("${google.pageName}")
    String pageName;

    @Getter
    @Value("${google.spreadsheetTemplate}")
    String spreadsheetTemplate;

    @Getter
    @Value("${google.imageTemplate}")
    String imageTemplate;

    @Scheduled(fixedRate = updatePeriod)
    @Async
    public void checkSpreadSheetUpdates() throws IOException, InterruptedException {
        URI url = getSpreadSheetUrl();
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(url).header("Accept", "application/json").build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(response.body());
        JsonObject rootObject = element.getAsJsonObject();
        JsonArray rows = rootObject.getAsJsonArray("values");

        if (rows.size() <= 1) {
            return;
        }

//                        for (int index = 1; index < rows.size(); index++) {
        for (int index = 1; index < 10; index++) {
            parseSpreadSheet(client, parser, rows, index);
        }

    }

    private void parseSpreadSheet(HttpClient client, JsonParser parser, JsonArray rows, int index) {
        try {
            JsonArray rowValues = rows.get(index).getAsJsonArray();
            getRowValues(client, parser, rowValues);
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void getRowValues(HttpClient client, JsonParser parser, JsonArray rowValues) throws IOException, InterruptedException {
        Long unixTimeCreateRecord = getUnixTimeCreateRecord(rowValues);
        String bloggerNickname = getPossibleValue(rowValues, 1);
        String restaurantName = getPossibleValue(rowValues, 3);
        String dishName = getPossibleValue(rowValues, 6);

        checkRowInDB(client, parser, rowValues, unixTimeCreateRecord, bloggerNickname, restaurantName, dishName);
    }

    private void checkRowInDB(HttpClient client, JsonParser parser, JsonArray rowValues,
                              Long unixTimeCreateRecord, String bloggerNickname, String restaurantName, String dishName) throws IOException, InterruptedException {
        Optional<GoogleSpreadSheet> itemInDB = googleSpreadSheetRepository
                .findByBloggerNicknameAndDateTimeOfRecordAndRestaurantNameAndDishName(bloggerNickname, unixTimeCreateRecord,
                        restaurantName, dishName);


        if (itemInDB.isEmpty()) {
            String bloggerURL = getPossibleValue(rowValues, 2);
            String averageCheck = getPossibleValue(rowValues, 4);
            String restaurantAddress = getPossibleValue(rowValues, 5);
            String dishDescription = getPossibleValue(rowValues, 7);
            Float dishPrice = Float.parseFloat(getPossibleValue(rowValues, 8));
            String dishCategory = getPossibleValue(rowValues, 9);
            String dishKitchenDirection = getPossibleValue(rowValues, 10);
            String dishPhotoLink = getPossibleValue(rowValues, 11);

            saveGoogleSpreadSheetRow(unixTimeCreateRecord, bloggerNickname, restaurantName, dishName, bloggerURL,
                    restaurantAddress, dishDescription, dishPrice, dishCategory, dishKitchenDirection, averageCheck, dishPhotoLink);

            DishCategory dishCategoryEntity = saveDishCategory(dishCategory);
            DishKitchenDirection dishKitchenDirectionEntity = saveDishKitchenDirection(dishKitchenDirection);
            Restaurant restaurant = saveRestaurant(restaurantName, restaurantAddress, averageCheck);


            URI photoUrl = getPhotoUrl(dishPhotoLink.substring(dishPhotoLink.indexOf(linkMatcher) + linkMatcher.length()));
            HttpRequest photoRequest = HttpRequest.newBuilder(photoUrl).header("Accept", "application/json").build();
            byte[] photoBytes = getPhotoBytes(client, parser, photoRequest);

            saveDish(dishName, dishDescription, dishPrice, dishCategoryEntity, dishKitchenDirectionEntity,
                    restaurant, photoBytes);
        }
    }

    void saveDish(String dishName, String dishDescription, Float dishPrice, DishCategory dishCategoryEntity,
                  DishKitchenDirection dishKitchenDirectionEntity, Restaurant restaurant, byte[] photoBytes) {
        dishRepository.getDishByNameAndRestaurant_NameAndRestaurant_Address(dishName, restaurant.getName(), restaurant.getAddress())
                .orElseGet(() -> {
                    Dish dishDB = new Dish();
                    dishDB.setName(dishName);
                    dishDB.setDescription(dishDescription);
                    dishDB.setPrice(dishPrice);
                    dishDB.setCategory(dishCategoryEntity);
                    dishDB.setKitchenDirection(dishKitchenDirectionEntity);
                    dishDB.setRestaurant(restaurant);
                    dishDB.setImage(photoBytes);
                    return dishRepository.save(dishDB);
                });
    }

    Restaurant saveRestaurant(String restaurantName, String restaurantAddress, String averageCheck) {
        return restaurantRepository.findByNameAndAddress(restaurantName, restaurantAddress)
                .orElseGet(() -> {
                    Restaurant restaurantDB = new Restaurant();
                    restaurantDB.setName(restaurantName);
                    restaurantDB.setAddress(restaurantAddress);
                    restaurantDB.setAverageCheck(averageCheck);
                    HashMap<String, Float> coordinates = restaurantDB.getCoordinatesFromYandex();

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

    void saveGoogleSpreadSheetRow(Long unixTimeCreateRecord, String bloggerNickname, String restaurantName, String dishName,
                                  String bloggerURL, String restaurantAddress, String dishDescription, Float dishPrice,
                                  String dishCategory, String dishKitchenDirection, String averageCheck, String dishPhotoLink) {
        GoogleSpreadSheet resultItem = new GoogleSpreadSheet();
        resultItem.setDateTimeOfRecord(unixTimeCreateRecord);
        resultItem.setBloggerNickname(bloggerNickname);
        resultItem.setBloggerUrl(bloggerURL);
        resultItem.setRestaurantName(restaurantName);
        resultItem.setRestaurantAddress(restaurantAddress);
        resultItem.setDishName(dishName);
        resultItem.setDishDescription(dishDescription);
        resultItem.setDishPrice(dishPrice);
        resultItem.setDishCategory(dishCategory);
        resultItem.setDishKitchen(dishKitchenDirection);
        resultItem.setAverageCheck(averageCheck);
        resultItem.setDishPhotoUrl(dishPhotoLink);

        googleSpreadSheetRepository.save(resultItem);
    }

    byte[] getPhotoBytes(HttpClient client, JsonParser parser, HttpRequest photoRequest) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(photoRequest, HttpResponse.BodyHandlers.ofString());

        JsonElement photoElement = parser.parse(response.body());
        JsonObject rootPhotoObject = photoElement.getAsJsonObject();
        String downloadUrl = rootPhotoObject.get("downloadUrl").getAsString();
        Image image = null;
        try {
            image = ImageIO.read(new URL(downloadUrl));
        } catch (IOException e) {
            log.error(e);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            if (image != null) {
                ImageIO.write((RenderedImage) image, "jpeg", baos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    private String getPossibleValue(JsonArray rowValues, int index) {
        try {
            return rowValues.get(index).getAsString().trim();
        } catch (Exception e) {
            log.error("Error with parsing Google Spreadsheet data: " + e);
            return "";
        }

    }

    private Long getUnixTimeCreateRecord(JsonArray rowValues) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = null;
        Long unixTimeCreateRecord = null;
        try {
            date = dateFormat.parse(getPossibleValue(rowValues, 0));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            unixTimeCreateRecord = date.getTime() / 1000;
        }
        return unixTimeCreateRecord;
    }

    private URI getSpreadSheetUrl() {
        UriTemplate spreadsheetTemplate = new UriTemplate(getSpreadsheetTemplate());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("spreadsheetId", spreadsheetId);
        parameters.put("pageName", pageName);
        parameters.put("apiKey", key);

        return spreadsheetTemplate.expand(parameters);
    }

    private URI getPhotoUrl(String imageId) {
        UriTemplate photoTemplate = new UriTemplate(getImageTemplate());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("imageId", imageId);
        parameters.put("apiKey", key);

        return photoTemplate.expand(parameters);
    }
}
