package com.src.choosebotapi.data.model.restaurant;

import com.src.choosebotapi.data.model.DefaultEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity(name = "dish_category")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table
@Getter
@Setter
public class DishCategory extends DefaultEntity {

    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dish_category_generator")
    Long id;

    @Column(unique = true)
    @NotEmpty @NotNull
    String name;
}
