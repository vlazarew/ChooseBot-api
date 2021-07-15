package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.DishCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
public interface DishCategoryRepository extends CrudRepository<DishCategory, Long> {
}
