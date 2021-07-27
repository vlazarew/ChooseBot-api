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
    @OneToOne(cascade = CascadeType.MERGE)
    DishCategory category;

    @JoinColumn(name = "dish_type")
    @OneToOne(cascade = CascadeType.MERGE)
    DishType type;

    @JoinColumn(name = "dish_kitchen_direction")
    @OneToOne(cascade = CascadeType.MERGE)
    DishKitchenDirection kitchenDirection;

    @JoinColumn(name = "restaurant")
    @OneToOne(cascade = CascadeType.MERGE)
    Restaurant restaurant;

    @DecimalMin("0") @Positive @NotNull Float price;

    @Lob
    byte[] image;
}
