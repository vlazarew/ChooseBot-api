package com.src.choosebotapi.data.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity(name = "dish")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dish")
@Getter
@Setter
public class Dish {

    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @NotEmpty @NotNull String name;
    @NotEmpty @NotNull String description;

    @NotNull
    @JoinColumn(name = "dish_id")
    @ManyToOne(cascade = {CascadeType.ALL})
    DishCategory category;


    @DecimalMin("0") @Positive @NotNull Double price;

    @Lob
    byte[] image;
}
