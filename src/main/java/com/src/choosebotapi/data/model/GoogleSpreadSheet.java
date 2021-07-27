package com.src.choosebotapi.data.model;

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
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    Long dateTimeOfRecord;
    String bloggerNickname;
    String bloggerUrl;
    String restaurantName;
    String restaurantAddress;
    String dishName;
    String dishDescription;
    Float dishPrice;
    String dishCategory;
    String dishKitchen;
    String dishType;
    String dishPhotoUrl;
}
