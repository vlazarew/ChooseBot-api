package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.TelegramContact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
public interface TelegramContactRepository extends CrudRepository<TelegramContact, Long> {
}
