package com.src.choosebotapi.data.repository.telegram;

import com.src.choosebotapi.data.model.telegram.TelegramMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RepositoryRestController
public interface TelegramMessageRepository extends CrudRepository<TelegramMessage, Long> {

    ArrayList<TelegramMessage> findTelegramMessageByText(String text);

}
