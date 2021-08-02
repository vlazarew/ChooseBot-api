package com.src.choosebotapi.data.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity(name = "dish")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table
@Getter
@Setter
public class Dish extends DefaultEntity {

    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dish_generator")
    Long id;

    @NotEmpty @NotNull String name;
    String description;

    @NotNull
    @JoinColumn(name = "dish_category")
    @ManyToOne(cascade = CascadeType.MERGE)
    DishCategory category;

    @JoinColumn(name = "dish_kitchen_direction")
    @ManyToOne(cascade = CascadeType.MERGE)
    DishKitchenDirection kitchenDirection;

    @ManyToOne(cascade = CascadeType.MERGE)
    Restaurant restaurant;

    @DecimalMin("0") @Positive @NotNull Float price;

    @Lob
    byte[] image;
}
