package com.src.choosebotapi.data.model.restaurant;

import com.src.choosebotapi.data.model.DefaultEntity;
import com.src.choosebotapi.service.yandex.YandexGeoDecoderService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;


@Entity(name = "restaurant")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = @Index(name = "averageCheckIndex", columnList = "averageCheck"))
@Getter
@Setter
@Log4j2
public class Restaurant extends DefaultEntity {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restaurant_generator")
    @Id
    Long id;

    @NotNull
    String name;

    @Column(length = 1000, name = "description")
    String description;

    String address;

    String averageCheck;

    Float longitude;
    Float latitude;

    @Lob
    byte[] image;

    @Override
    public void toCreate() {
        super.toCreate();
    }

    public HashMap<String, Float> getCoordinatesFromYandex() {
        YandexGeoDecoderService yandexGeoCoderService = new YandexGeoDecoderService();
        HashMap<String, Float> coordinates = yandexGeoCoderService.getCoordinatesByAddress(this.getAddress());
        try {
            this.setLatitude(coordinates.get("latitude"));
            this.setLongitude(coordinates.get("longitude"));
        } catch (Exception e) {
            log.error("Координаты для ресторана " + this.getName() + " не подобраны.");
            this.setLongitude(0F);
            this.setLatitude(0F);
            return new HashMap<>() {{
                put("latitude", 0F);
                put("longitude", 0F);
            }};
        }

        return coordinates;
    }
}
