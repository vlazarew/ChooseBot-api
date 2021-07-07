package com.src.choosebotapi.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Entity(name = "dish")
@Data
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
    @NotEmpty @NotNull String description;

    @NotNull
    @JoinColumn(name = "dish_id")
    @ManyToOne(cascade = {CascadeType.ALL})
    DishCategory category;


    @DecimalMin("0") @Positive @NotNull Double price;

    @Lob
    byte[] image;
}
