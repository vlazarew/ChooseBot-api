package com.src.choosebotapi.service.yandex;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.src.choosebotapi.data.model.restaurant.Restaurant;
import com.src.choosebotapi.data.model.restaurant.Session;
import com.src.choosebotapi.data.model.telegram.TelegramLocation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource("classpath:yandex.properties")
public class YandexGeoDecoderService {
    static String apiKey = null;
    static String urlTemplate = null;
    static String mapUrlTemplate = null;

    @Value("${yandex.key}")
    public void setApiKey(String value) {
        apiKey = value;
    }

    @Value("${yandex.url}")
    public void setUrlTemplate(String value) {
        urlTemplate = value;
    }

    @Value("${yandex.mapUrl}")
    public void setMapUrlTemplate(String value) {
        mapUrlTemplate = value;
    }


    public HashMap<String, Float> getCoordinatesByAddress(String address) {
        CompletableFuture<URI> url = CompletableFuture.completedFuture(getTemplate(address));
        HttpClient client = HttpClient.newHttpClient();

        return url.thenCompose(uri -> {
            HttpRequest request = HttpRequest.newBuilder(uri).header("Accept", "application/json").build();
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(
                    response -> {
                        if (response.statusCode() == 200) {
                            JsonObject geoObject = null;
                            try {
                                geoObject = getJSONObject(response.body(), address);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (geoObject == null) {
                                log.error("geoObject is null");
                                return null;
                            }

                            JsonObject point = geoObject.getAsJsonObject("Point");

                            String pos = point.get("pos").getAsString();
                            String[] posArray = pos.split(" ");

                            HashMap<String, Float> resultMap = new HashMap<String, Float>();
                            resultMap.put("longitude", Float.valueOf(posArray[0]));
                            resultMap.put("latitude", Float.valueOf(posArray[1]));

                            return resultMap;
                        } else {
                            log.error("Сервис не отвечает");
                            return null;
                        }
                    });
        }).join();
    }


    private URI getTemplate(String address) {
        UriTemplate template = new UriTemplate(urlTemplate);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("key", apiKey);
        parameters.put("address", address);
        return template.expand(parameters);
    }

    public URI makeRouteUrl(Session session) {
        UriTemplate template = new UriTemplate(mapUrlTemplate);
        Map<String, String> parameters = new HashMap<>();
        TelegramLocation location = session.getLocation();
        parameters.put("latitudeStart", location.getLatitude().toString());
        Restaurant restaurant = session.getDish().getRestaurant();
        parameters.put("latitudeEnd", restaurant.getLatitude().toString());
        parameters.put("longitudeStart", location.getLongitude().toString());
        parameters.put("longitudeEnd", restaurant.getLongitude().toString());
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
    private static JsonObject getJSONObject(String body, String address) throws IOException {
//        String str = new String(IOUtils.toByteArray(response.getEntity().getContent()), StandardCharsets.UTF_8);

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(body);

        // Начинаем парсить
        JsonObject rootObject = element.getAsJsonObject();
        JsonObject responseObject = rootObject.getAsJsonObject("response");
        JsonObject geoObjectCollectionObject = responseObject.getAsJsonObject("GeoObjectCollection");
        JsonArray featureMemberObject = geoObjectCollectionObject.getAsJsonArray("featureMember");

        if (featureMemberObject.size() == 0) {
            log.error("По указанному адресу (" + address + " не найдено координат");
            return null;
        } else if (featureMemberObject.size() > 1) {
            log.warn("По указанному адресу (" + address + " вернули более 1 координат");
        }

        JsonObject firstAddressBlock = featureMemberObject.get(0).getAsJsonObject();

        return firstAddressBlock.getAsJsonObject("GeoObject");
    }

}
