package com.src.choosebotapi.service.google;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import lombok.Getter;
import lombok.Synchronized;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@EnableScheduling
@EnableAsync
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource("classpath:google.properties")
public class SaveInfoFromGoogleSpreadSheetService {

    final String linkMatcher = "?id=";

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

    @Getter
    @Value("${google.key}")
    String key;

    @Getter
    @Value("${google.imageTemplate}")
    String imageTemplate;

//    @Scheduled(fixedDelay = 5000)
    @Scheduled(cron = "00 15,30,45,00 * * * *")
    @Async
    @Synchronized
    public void saveInfoFromGoogleSpreadSheet() {

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
                    URI photoUrl = new URI("");
                    try {
                        photoUrl = getPhotoUrl(row.getDishPhotoUrl().substring(row.getDishPhotoUrl().indexOf(linkMatcher) + linkMatcher.length()));
                    } catch (Exception e) {
                        log.error("Ошибка при получении url изображения блюда. Строка " + row.getRowIndex() + ". Описание ошибки: " + e.getMessage());
                    }

                    if (!photoUrl.toString().equals("")) {
                        HttpGet httpGet = new HttpGet(photoUrl.toString());
                        photoBytes = getPhotoBytes(httpGet, row.getRowIndex(), true);
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
            Dish dish = dishOptional.get();
            return !row.getDishName().equals(dish.getName()) || !row.getDishCategory().equals(dish.getCategory().getName())
                    || !row.getAverageCheck().equals(dish.getRestaurant().getAverageCheck()) || !row.getDishPrice().equals(dish.getPrice())
                    || !row.getDishDescription().equals(dish.getDescription()) || !row.getDishKitchen().equals(dish.getKitchenDirection().getName())
                    || !row.getRestaurantAddress().equals(dish.getRestaurant().getAddress()) || !row.getRestaurantName().equals(dish.getRestaurant().getName())
                    || dish.getImage() == null;
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
//                    return restaurantRepository.save(restaurantDB);
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

    @Async
    byte[] getPhotoBytes(HttpGet httpGet, int rowIndex, boolean useProxy) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(httpGet)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String str = new String(IOUtils.toByteArray(response.getEntity().getContent()), StandardCharsets.UTF_8);

            if (statusCode == 200) {
                JsonParser parser = new JsonParser();
                JsonElement photoElement = parser.parse(str);
                JsonObject rootPhotoObject = photoElement.getAsJsonObject();
                String downloadUrl = rootPhotoObject.get("downloadUrl").getAsString();
                Image image;
                try {
                    log.info("downloading image for row " + rowIndex);
                    image = getImage(downloadUrl, useProxy);
                } catch (Exception e) {
                    log.error(e);
                    return null;
                }

                if (image == null) {
                    return getPhotoBytes(httpGet, rowIndex, !useProxy);
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ImageIO.write((RenderedImage) image, "jpeg", baos);
                } catch (Exception e) {
                    log.error(e);
                    return null;
                }
                return baos.toByteArray();
            } else {
                log.error("Не удалось получить фотографию блюда. Код ответа: " + statusCode + " Описание ошибки: " + str);
                return null;
            }
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    private BufferedImage getImage(String url, boolean useProxy) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);

            if (useProxy) {
                HttpHost proxy = new HttpHost("188.133.153.201", 1256, "http");

                RequestConfig config = RequestConfig.custom()
                        .setProxy(proxy)
                        .build();
                request.setConfig(config);
            }

            try {
                CloseableHttpResponse response = httpclient.execute(request);
                return ImageIO.read(response.getEntity().getContent());
            } catch (Exception e) {
                log.warn(e.getMessage());
            } finally {
                httpclient.close();
            }
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    private URI getPhotoUrl(String imageId) {
        UriTemplate photoTemplate = new UriTemplate(getImageTemplate());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("imageId", imageId);
        parameters.put("apiKey", key);

        return photoTemplate.expand(parameters);
    }

}
