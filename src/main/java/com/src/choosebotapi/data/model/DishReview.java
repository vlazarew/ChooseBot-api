package com.src.choosebotapi.data.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;

@Entity(name = "dish_review")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table
@Getter
@Setter
@Log4j2
public class DishReview extends DefaultEntity{

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dish_review_generator")
    @Id
    Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    TelegramUser user;

    Long value;
}
