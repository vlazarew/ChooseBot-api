package com.src.choosebotapi.controller;

import com.src.choosebotapi.data.model.telegram.TelegramUser;
import com.src.choosebotapi.data.model.telegram.UserStatus;
import com.src.choosebotapi.data.repository.telegram.TelegramUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RequestMapping(value = "/users", produces = "application/json")
@CrossOrigin("*")
@EnableAsync
@Log4j2
public class TelegramUserController {

    @Autowired
    TelegramUserRepository telegramUserRepository;

    @PutMapping(path = "updateStatus", params = {"userId", "statusId"}, produces = "application/json")
    @Async
    public CompletableFuture<ResponseEntity<?>> setNewStatusForUser(@RequestParam("userId") Long userId,
                                                                    @RequestParam("statusId") Long statusId) {
        Optional<TelegramUser> telegramUserOptional = telegramUserRepository.findById(userId);
        if (telegramUserOptional.isEmpty()) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found"));
        }

        TelegramUser user = telegramUserOptional.get();
        return CompletableFuture.completedFuture(updateStatusForUser(statusId, user));
    }

    private ResponseEntity<String> updateStatusForUser(Long statusId, TelegramUser user) {
        UserStatus status;
        try {
            status = UserStatus.byId(Math.toIntExact(statusId));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("wrong UserStatus id. " + exception.getMessage());
        }

        user.setStatus(status);
        telegramUserRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body("successfully updated");
    }

    @PutMapping(path = "updateStatusForAllUsers", params = {"statusId"}, produces = "application/json")
    @Async
    public CompletableFuture<ResponseEntity<?>> setNewStatusForAllUsers(@RequestParam("statusId") Long statusId) {
        ArrayList<TelegramUser> telegramUsers = (ArrayList<TelegramUser>) telegramUserRepository.findAll();

        for (TelegramUser user : telegramUsers) {
            ResponseEntity<String> responseEntity = updateStatusForUser(statusId, user);
            if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
                return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseEntity.getBody()));
            }
        }

        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.OK).body("successfully updated"));
    }
}
