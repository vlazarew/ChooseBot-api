package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.Dish;
import com.src.choosebotapi.data.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@RepositoryRestController
public interface DishRepository extends JpaRepository<Dish, Long> {
    Optional<Dish> getDishByNameAndRestaurant_NameAndRestaurant_Address(@NotEmpty @NotNull String name, @NotNull String restaurant_name, String restaurant_address);
}