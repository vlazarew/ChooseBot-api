package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.TelegramChat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
public interface TelegramChatRepository extends CrudRepository<TelegramChat, Long> {
}
