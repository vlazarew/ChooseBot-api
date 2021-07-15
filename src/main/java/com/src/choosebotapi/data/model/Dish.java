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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @NotEmpty @NotNull String name;
    String description;

    @NotNull
    @JoinColumn(name = "dish_id")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    DishCategory category;


    @DecimalMin("0") @Positive @NotNull Double price;

    @Lob
    byte[] image;
}
