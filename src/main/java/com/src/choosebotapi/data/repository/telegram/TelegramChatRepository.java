package com.src.choosebotapi.data.repository.telegram;

import com.src.choosebotapi.data.model.telegram.TelegramChat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
public interface TelegramChatRepository extends CrudRepository<TelegramChat, Long> {
    TelegramChat findByUserId(Integer id);
}
