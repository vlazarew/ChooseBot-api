package com.src.choosebotapi.data.model.restaurant;

import com.src.choosebotapi.data.model.DefaultEntity;
import com.src.choosebotapi.data.model.google.GoogleSpreadSheet;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Entity(name = "dish")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = @Index(name = "dishNameIndex", columnList = "name"))
@Getter
@Setter
public class Dish extends DefaultEntity {

    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dish_generator")
    Long id;

    @NotEmpty
    @NotNull
    String name;

    @Column(length = 1000, name = "description")
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

    @OneToOne
    GoogleSpreadSheet googleSpreadSheetRow;

    @OneToMany
    List<DishReview> reviewList;
}
