package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.TelegramUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
public interface TelegramUserRepository extends CrudRepository<TelegramUser, Long> {
}
