package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@RepositoryRestController
public interface DishRepository extends JpaRepository<Dish, Long> {
    Optional<Dish> getDishByNameAndRestaurant_NameAndRestaurant_Address(@NotEmpty @NotNull String name, @NotNull String restaurant_name, String restaurant_address);

    @Query(value = "select ifnull(sum(dr.value), 0) as summary, count(dr.value) as count, dish.id as id\n" +
            "from dish\n" +
            "         left join dish_review_list drl on dish.id = drl.dish_id\n" +
            "         left join dish_review dr on dr.id = drl.review_list_id\n" +
            "group by dish.id\n" +
            "order by (sum(dr.value) / count(dr.value)) desc\n" +
            "limit 10;", nativeQuery = true)
    List<Object> findTop10ByRating();
}