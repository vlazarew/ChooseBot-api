package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.PromoCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
public interface PromoCodeRepository extends CrudRepository<PromoCode, Long> {
}
