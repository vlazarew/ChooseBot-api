package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.TelegramMessage;
import com.src.choosebotapi.data.model.TelegramUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.scheduling.annotation.Async;

@RepositoryRestController
public interface TelegramMessageRepository extends CrudRepository<TelegramMessage, Long> {
    @Async
    public <S extends TelegramMessage> S save(S message);
}
