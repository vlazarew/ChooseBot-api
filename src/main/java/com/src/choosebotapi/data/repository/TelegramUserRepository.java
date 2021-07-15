package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.TelegramUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.scheduling.annotation.Async;

@RepositoryRestController
public interface TelegramUserRepository extends CrudRepository<TelegramUser, Long> {
    @Async
    public <S extends TelegramUser> S save(S user);
}
