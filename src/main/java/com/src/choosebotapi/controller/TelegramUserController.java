package com.src.choosebotapi.controller;

import com.src.choosebotapi.data.model.telegram.TelegramUser;
import com.src.choosebotapi.data.model.telegram.UserStatus;
import com.src.choosebotapi.data.repository.telegram.TelegramUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RequestMapping(value = "/users", produces = "application/json")
@CrossOrigin("*")
public class TelegramUserController {

    @Autowired
    TelegramUserRepository telegramUserRepository;

    @PutMapping(path = "updateStatus", params = {"userId", "statusId"}, produces = "application/json")
    public ResponseEntity<?> setNewStatusForUser(@RequestParam("userId") Long userId,
                                              @RequestParam("statusId") Long statusId) {
        Optional<TelegramUser> telegramUserOptional = telegramUserRepository.findById(userId);
        if (telegramUserOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }

        TelegramUser user = telegramUserOptional.get();
        return updateStatusForUser(statusId, user);
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
    public ResponseEntity<?> setNewStatusForAllUsers(@RequestParam("statusId") Long statusId) {
        ArrayList<TelegramUser> telegramUsers = (ArrayList<TelegramUser>) telegramUserRepository.findAll();

        for (TelegramUser user : telegramUsers) {
            ResponseEntity<String> responseEntity = updateStatusForUser(statusId, user);
            if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseEntity.getBody());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body("successfully updated");
    }
}
