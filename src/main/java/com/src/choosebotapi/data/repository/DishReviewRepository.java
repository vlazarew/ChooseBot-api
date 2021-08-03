package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.DishReview;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
public interface DishReviewRepository extends CrudRepository<DishReview, Long> {
}
