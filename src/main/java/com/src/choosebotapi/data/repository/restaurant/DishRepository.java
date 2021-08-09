package com.src.choosebotapi.data.repository.restaurant;

import com.src.choosebotapi.data.model.restaurant.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@RepositoryRestController
public interface DishRepository extends JpaRepository<Dish, Long> {
    Optional<Dish> getDishByNameAndRestaurant_NameAndRestaurant_Address(@NotEmpty @NotNull String name, @NotNull String restaurant_name, String restaurant_address);

    @Query(value = "with closest_restaurant(distance, id, name, average_check) as (\n" +
            "    select st_distance_sphere(POINT(:start_latitude, :start_longitude), POINT(restaurant.latitude, restaurant.longitude)) as distance,\n" +
            "           restaurant.id,\n" +
            "           restaurant.name,\n" +
            "           restaurant.average_check\n" +
            "    from restaurant\n" +
            "    where st_distance_sphere(POINT(:start_latitude, :start_longitude), POINT(restaurant.latitude, restaurant.longitude)) < 10000\n" +
            "    order by st_distance_sphere(POINT(:start_latitude, :start_longitude), POINT(restaurant.latitude, restaurant.longitude)))\n" +
            "\n" +
            "\n" +
            "select ifnull(sum(dr.value), 0) as summary,\n" +
            "       count(dr.value)          as count,\n" +
            "       dish.id                  as id,\n" +
            "       dish.name                as name,\n" +
            "       cr.id                    as restaurant_id,\n" +
            "       cr.name                  as restaurant_name,\n" +
            "       cr.distance              as distance,\n" +
            "       cr.average_check         as average_check\n" +
            "from dish\n" +
            "         left join dish_review_list drl on dish.id = drl.dish_id\n" +
            "         left join dish_review dr on dr.id = drl.review_list_id\n" +
            "         join closest_restaurant cr on cr.id = dish.restaurant_id\n" +
            "where IF(:dish_template = :dish_template is null, true,\n" +
            "         lower(dish.name) like lower(CONCAT('%', :dish_template, '%')))\n" +
            "  and IF(:average_check = :average_check is null, true,\n" +
            "         lower(cr.average_check) = lower(:average_check))\n" +
            "  and IF(:dish_category = :dish_category is null, true,\n" +
            "         lower(dish.dish_category) = :dish_category)\n" +
            "  and IF(:dish_kitchen_direction = :dish_kitchen_direction is null, true,\n" +
            "         lower(dish.dish_kitchen_direction) = :dish_kitchen_direction)\n" +
            "group by dish.id, dish.name, cr.distance\n" +
            "order by (sum(dr.value) / count(dr.value))\n" +
            "        desc, cr.distance\n" +
            "limit 10;\n", nativeQuery = true)
    List<Object> findTop10ByRating(@Param("dish_template") String dishTemplate,
                                   @Param("average_check") String averageCheck,
                                   @Param("dish_category") Long dishCategory,
                                   @Param("dish_kitchen_direction") Long dishKitchenDirection,
                                   @Param("start_latitude") Float startLatitude,
                                   @Param("start_longitude") Float startLongitude);
}