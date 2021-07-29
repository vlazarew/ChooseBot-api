package com.src.choosebotapi.data.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
class GoogleSpreadSheetId implements Serializable {
    Long dateTimeOfRecord;
    String bloggerNickname;
    String restaurantName;
    String dishName;
}

@Entity(name = "google_spreadsheet")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table
@Getter
@Setter
@IdClass(GoogleSpreadSheetId.class)
public class GoogleSpreadSheet extends DefaultEntity {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "google_spreadsheet_generator")
    Long id;

    @Id
    Long dateTimeOfRecord;
    @Id
    String bloggerNickname;
    String bloggerUrl;
    @Id
    String restaurantName;
    String restaurantAddress;
    @Id
    String dishName;
    String dishDescription;
    Float dishPrice;
    String dishCategory;
    String dishKitchen;
    String dishType;
    String dishPhotoUrl;
}
