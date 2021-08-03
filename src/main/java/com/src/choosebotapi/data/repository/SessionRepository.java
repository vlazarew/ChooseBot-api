package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.Session;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
public interface SessionRepository extends CrudRepository<Session, Long> {
}
