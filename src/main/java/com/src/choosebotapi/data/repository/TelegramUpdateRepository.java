package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.TelegramUpdate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

@RepositoryRestController
public interface TelegramUpdateRepository extends CrudRepository<TelegramUpdate, Long> {
    @Async
    public <S extends TelegramUpdate> CompletableFuture<S> save(S update);
}
