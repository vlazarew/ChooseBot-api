package com.src.choosebotapi.data.repository.telegram;

import com.src.choosebotapi.data.model.telegram.TelegramUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import java.util.Optional;

@RepositoryRestController
public interface TelegramUserRepository extends CrudRepository<TelegramUser, Long> {

    Optional<TelegramUser> findById(Long id);

}
