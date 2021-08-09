package com.src.choosebotapi.data.repository.restaurant;

import com.src.choosebotapi.data.model.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@RepositoryRestController
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByNameAndAddress(@NotNull String name, String address);

    Optional<Restaurant> findByNameAndLongitudeAndLatitude(@NotNull String name, Float longitude, Float latitude);
}