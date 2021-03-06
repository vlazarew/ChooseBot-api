package com.src.choosebotapi.data.model.telegram;

import com.src.choosebotapi.data.model.telegram.AbstractTelegramEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity(name = "telegram_location")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table
@Getter
@Setter
public class TelegramLocation extends AbstractTelegramEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "telegram_location_generator")
    Long id;

    @Column(name = "longitude")
    Float longitude;
    @Column(name = "latitude")
    Float latitude;
    String city;
}
