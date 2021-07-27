package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.DishKitchenDirection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@RepositoryRestController
public interface DishKitchenDirectionRepository extends CrudRepository<DishKitchenDirection, Long> {
    Optional<DishKitchenDirection> findByName(@NotEmpty @NotNull String name);
}
