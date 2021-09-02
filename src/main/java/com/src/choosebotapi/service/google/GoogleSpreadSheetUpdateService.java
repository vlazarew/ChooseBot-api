package com.src.choosebotapi.service.google;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.src.choosebotapi.data.model.google.GoogleSpreadSheet;
import com.src.choosebotapi.data.repository.google.GoogleSpreadSheetRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Synchronized;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
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

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Log4j2
@Service
@EnableScheduling
@EnableAsync
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource("classpath:google.properties")
public class GoogleSpreadSheetUpdateService {

    //    final long updatePeriod = 5000;

    @Autowired
    GoogleSpreadSheetRepository googleSpreadSheetRepository;

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

        @Scheduled(cron = "00 15,30,45,00 * * * *")
//    @Scheduled(fixedDelay = 5000)
    @Async
    @Synchronized
    public void checkSpreadSheetUpdates() {

        long countOfRecords = googleSpreadSheetRepository.count();

        URI url = getSpreadSheetUrl();
        HttpGet httpGet = new HttpGet(url.toString());
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(httpGet)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String str = new String(IOUtils.toByteArray(response.getEntity().getContent()), StandardCharsets.UTF_8);

            if (statusCode == 200) {
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(str);
                JsonObject rootObject = element.getAsJsonObject();
                JsonArray rows = rootObject.getAsJsonArray("values");

                if (rows.size() <= 1) {
                    return;
                }

                long dishIndex = countOfRecords + 1;
                if (dishIndex < rows.size()) {
                    for (int index = (int) dishIndex; index < rows.size(); index++) {
                        parseSpreadSheet(rows, index);
                    }
                }
            } else {
                log.error("Не удалость загрузить данные Google таблицы с блюдами. Код ответа: " + statusCode);
            }
        } catch (IOException e) {
            log.error(e);
        }

    }

    private void parseSpreadSheet(JsonArray rows, int index) {
        try {
            JsonArray rowValues = rows.get(index).getAsJsonArray();
            getRowValues(rowValues, index);
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void getRowValues(JsonArray rowValues,  int rowIndex) {
        if (getPossibleValue(rowValues, 0, false, rowIndex).equals("")) {
            return;
        }
        Long unixTimeCreateRecord = getUnixTimeCreateRecord(rowValues, rowIndex);
        String bloggerNickname = getPossibleValue(rowValues, 1, false, rowIndex);
        String bloggerURL = getPossibleValue(rowValues, 2, false, rowIndex);
        String restaurantName = getPossibleValue(rowValues, 3, false, rowIndex);
        String averageCheck = getPossibleValue(rowValues, 4, true, rowIndex);
        String restaurantAddress = getPossibleValue(rowValues, 5, false, rowIndex);
        String dishName = getPossibleValue(rowValues, 6, true, rowIndex);
        String dishDescription = getPossibleValue(rowValues, 7, true, rowIndex);
        Float dishPrice = Float.parseFloat(getPossibleValue(rowValues, 8, false, rowIndex));
        String dishCategory = getPossibleValue(rowValues, 9, true, rowIndex);
        String dishKitchenDirection = getPossibleValue(rowValues, 10, true, rowIndex);
        String dishPhotoLink = getPossibleValue(rowValues, 11, false, rowIndex);

        saveGoogleSpreadSheetRow(unixTimeCreateRecord, bloggerNickname, restaurantName, dishName, bloggerURL,
                restaurantAddress, dishDescription, dishPrice, dishCategory, dishKitchenDirection, averageCheck, dishPhotoLink, rowIndex);
    }

    void saveGoogleSpreadSheetRow(Long unixTimeCreateRecord, String bloggerNickname, String restaurantName, String dishName,
                                  String bloggerURL, String restaurantAddress, String dishDescription, Float dishPrice,
                                  String dishCategory, String dishKitchenDirection, String averageCheck, String dishPhotoLink,
                                  int rowIndex) {
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
        resultItem.setRowIndex(rowIndex);

        googleSpreadSheetRepository.save(resultItem);
    }

    private String getPossibleValue(JsonArray rowValues, int index, boolean makeLower, int rowIndex) {
        try {
            String resultString = rowValues.get(index).getAsString().trim();
            if (makeLower) {
                String lowerTargetWord = resultString.toLowerCase(Locale.ROOT);
                if (lowerTargetWord.isEmpty()) {
                    return "";
                }
                return lowerTargetWord.substring(0, 1).toUpperCase() + lowerTargetWord.substring(1);
            }
            return resultString;
        } catch (Exception e) {
            log.error("Ошибка при чтении/парсинге Google Spreadsheet data (row " + rowIndex + "): " + e);
            return "";
        }

    }

    private Long getUnixTimeCreateRecord(JsonArray rowValues, int rowIndex) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = null;
        Long unixTimeCreateRecord = null;
        try {
            date = dateFormat.parse(getPossibleValue(rowValues, 0, false, rowIndex));
        } catch (ParseException e) {
            log.error(e);
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
}
