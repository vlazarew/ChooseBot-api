package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@RepositoryRestController
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByNameAndAddress(@NotNull String name, String address);
}
