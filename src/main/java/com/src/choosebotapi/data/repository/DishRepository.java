package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface DishRepository extends JpaRepository<Dish, Long> {
}