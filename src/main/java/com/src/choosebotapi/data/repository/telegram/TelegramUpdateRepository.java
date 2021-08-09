package com.src.choosebotapi.data.repository.telegram;

import com.src.choosebotapi.data.model.telegram.TelegramUpdate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
public interface TelegramUpdateRepository extends CrudRepository<TelegramUpdate, Long> {
}
