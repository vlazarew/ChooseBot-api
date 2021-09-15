package com.src.choosebotapi.data.repository.restaurant;

import com.src.choosebotapi.data.model.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@RepositoryRestController
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    @Query(value = "select restaurant.*" +
            "    from restaurant" +
            "    where lower(restaurant.name) = lower(:name)" +
            " and lower(restaurant.address) = lower(:address)", nativeQuery = true)
    Optional<Restaurant> findByNameAndAddress(@Param("name") String name,
                                                   @Param("address") String address);

    Optional<Restaurant> findByNameAndLongitudeAndLatitude(@NotNull String name, Float longitude, Float latitude);


    @Query(value = "select restaurant.*" +
            "    from restaurant" +
            "    where st_distance_sphere(POINT(:start_latitude, :start_longitude), POINT(restaurant.latitude, restaurant.longitude)) < :max_distance", nativeQuery = true)
    List<Restaurant> findClosestRestaurant(@Param("start_latitude") Float startLatitude,
                                           @Param("start_longitude") Float startLongitude,
                                           @Param("max_distance") Float maxDistance);
}
