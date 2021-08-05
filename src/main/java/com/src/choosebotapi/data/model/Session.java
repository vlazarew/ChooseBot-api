package com.src.choosebotapi.data.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.List;

@Entity(name = "session")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table
@Getter
@Setter
@Log4j2
public class Session extends DefaultEntity {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "session_generator")
    @Id
    Long id;

    @ManyToOne
    TelegramUser user;

    @OneToOne
    TelegramLocation location;

    String dishTemplate;
    String averageCheck;

    @ManyToOne(cascade = CascadeType.ALL)
    DishCategory dishCategory;

    @ManyToOne(cascade = CascadeType.ALL)
    DishKitchenDirection dishKitchenDirection;

    @ManyToOne(cascade = CascadeType.ALL)
    Dish dish;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Lazy
    List<Dish> dishesToSelect;

    @Min(value = 0)
    Integer dishIndexInList = 0;

    boolean sessionFinished = false;
    boolean notificationSend = false;
}
