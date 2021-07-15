package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
public interface DishRepository extends JpaRepository<Dish, Long> {
}