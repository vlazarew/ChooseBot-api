package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RepositoryRestResource
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
    @Async()
    <Long extends id> public CompletableFuture<Optional<TelegramUser>> findById(Long id);
}
