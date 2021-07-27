package com.src.choosebotapi.data.model;

import com.src.choosebotapi.service.yandex.YandexGeoDecoderService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.javatuples.Pair;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity(name = "restaurant")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table
@Getter
@Setter
public class Restaurant extends DefaultEntity {

    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restaurant_generator")
    Long id;

    @NotNull
    String name;

    String description;

    String address;

    Float longitude;
    Float latitude;

    @Lob
    byte[] image;

    @Override
    public void toCreate() {
        YandexGeoDecoderService yandexGeoCoderService = new YandexGeoDecoderService();
        super.toCreate();
        Pair<Float, Float> coordinates = yandexGeoCoderService.getCoordinatesByAddress(this.getAddress());
        this.setLatitude(coordinates.getValue0());
        this.setLongitude(coordinates.getValue1());
    }
}
