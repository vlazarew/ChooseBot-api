package com.src.choosebotapi.data.model.google;

import com.src.choosebotapi.data.model.DefaultEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;


@Entity(name = "google_spreadsheet")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table
@Getter
@Setter
public class GoogleSpreadSheet extends DefaultEntity {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "google_spreadsheet_generator")
    @Id
    Long id;

    Long dateTimeOfRecord;
    String bloggerNickname;
    String bloggerUrl;
    String restaurantName;
    String restaurantAddress;
    String dishName;
    @Column(length = 1000, name = "description")
    String dishDescription;
    Float dishPrice;
    String dishCategory;
    String dishKitchen;
    String averageCheck;
    String dishPhotoUrl;
    int rowIndex;
}
