package com.src.choosebotapi.service.yandex;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;



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
import org.springframework.transaction.annotation.Transactional;
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
import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
@EnableAsync
@EnableScheduling
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource("classpath:yandex.properties")
public class YandexGeoDecoderService {

    @Getter
    @Value("${yandex.key}")
    String apiKey;

    @Getter
    @Value("${yandex.url}")
    String urlTemplate;

    @Async
    public Pair<Float, Float> getCoordinatesByAddress(String address) {
        Pair<Float, Float> result = new Pair<Float, Float>(0F, 0F);

        CompletableFuture<URI> url = CompletableFuture.completedFuture(getTemplate(address));
        HttpClient client = HttpClient.newHttpClient();

        url.thenCompose(uri -> {
            HttpRequest request = HttpRequest.newBuilder(uri).header("Accept", "application/json").build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(
                    response -> {
                        return null;
                    });
            return null;
        });
        return null;
    }


    private URI getTemplate(String address) {
        UriTemplate template = new UriTemplate(getUrlTemplate());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("apiKey", apiKey);
        parameters.put("address", address);
        return template.expand(parameters);
    }
//    public String getCityByCoordinates(String coordinates) {
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        HttpGet request = createRequest(coordinates);
//
//        try (CloseableHttpResponse response = httpClient.execute(request)) {
//            int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode == 200) {
//
//                JsonObject geoObject = getJSONObject(response);
//
//                return (geoObject == null) ? null : geoObject.get("name").getAsString();
//            } else {
//                log.error("Сервис геокодирования не отвечает. Код ответа: " + statusCode);
//                return null;
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public HashMap<String, Float> getCoordinatesByCity(String city) {
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        HttpGet request = createRequest(city);
//
//        try (CloseableHttpResponse response = httpClient.execute(request)) {
//            if (response.getStatusLine().getStatusCode() == 200) {
//                JsonObject geoObject = getJSONObject(response);
//                if (geoObject == null) {
//                    return null;
//                }
//
//                JsonObject point = geoObject.getAsJsonObject("Point");
//
//                String pos = point.get("pos").getAsString();
//                String[] posArray = pos.split(" ");
//
//                HashMap<String, Float> resultMap = new HashMap<String, Float>();
//                resultMap.put("longitude", Float.valueOf(posArray[0]));
//                resultMap.put("latitude", Float.valueOf(posArray[1]));
//
//                return resultMap;
//            } else {
//                log.error("Сервис не отвечает");
//                return null;
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private HttpGet createRequest(String geocode) {
//        StringBuilder requestUrl = new StringBuilder(defaultUrl);
//
//        List<NameValuePair> urlParameters = getListUrlParameters(geocode);
//
//        urlParameters.forEach(nameValuePair -> requestUrl
//                .append(nameValuePair.getName())
//                .append("=")
//                .append(nameValuePair.getValue())
//                .append("&"));
//
//        requestUrl.deleteCharAt(requestUrl.length() - 1);
//
//        return new HttpGet(requestUrl.toString());
//    }
//
//    private List<NameValuePair> getListUrlParameters(String geocode) {
//        return new ArrayList<NameValuePair>() {{
//            add(new BasicNameValuePair("geocode", geocode));
//            add(new BasicNameValuePair("apikey", apiKey));
//            add(new BasicNameValuePair("format", "json"));
//            add(new BasicNameValuePair("kind", "locality"));
//            add(new BasicNameValuePair("results", "1"));
//        }};
//    }
//
//    private static JsonObject getJSONObject(CloseableHttpResponse response) throws IOException {
//        String str = new String(IOUtils.toByteArray(response.getEntity().getContent()), StandardCharsets.UTF_8);
//
//        JsonParser parser = new JsonParser();
//        JsonElement element = parser.parse(str);
//
//        // Начинаем парсить
//        JsonObject rootObject = element.getAsJsonObject();
//        JsonObject responseObject = rootObject.getAsJsonObject("response");
//        JsonObject geoObjectCollectionObject = responseObject.getAsJsonObject("GeoObjectCollection");
//        JsonArray featureMemberObject = geoObjectCollectionObject.getAsJsonArray("featureMember");
//
//        if (featureMemberObject.size() == 0) {
//            log.error("По указанным координатам не найдено адреса");
//            return null;
//        } else if (featureMemberObject.size() > 1) {
//            log.error("По указанным координатам вернули более 1 адреса");
//        }
//
//        JsonObject firstAddressBlock = featureMemberObject.get(0).getAsJsonObject();
//
//        return firstAddressBlock.getAsJsonObject("GeoObject");
//    }

}
