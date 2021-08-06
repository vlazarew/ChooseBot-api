package com.src.choosebotapi.data.repository.restaurant;

import com.src.choosebotapi.data.model.restaurant.DishCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@RepositoryRestController
public interface DishCategoryRepository extends CrudRepository<DishCategory, Long> {
    Optional<DishCategory> findByName(@NotEmpty @NotNull String name);
}
