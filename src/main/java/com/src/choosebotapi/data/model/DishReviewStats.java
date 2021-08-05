package com.src.choosebotapi.data.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DishReviewStats {
    Dish dish;
    Long summary;
    Long count;
}