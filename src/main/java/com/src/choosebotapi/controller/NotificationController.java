package com.src.choosebotapi.controller;

import com.src.choosebotapi.data.model.telegram.TelegramUser;
import com.src.choosebotapi.data.repository.telegram.TelegramUserRepository;
import com.src.choosebotapi.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RequestMapping(value = "/notification", produces = "application/json")
@CrossOrigin("*")
@PropertySource("classpath:telegram.properties")
@EnableAsync
@Log4j2
public class NotificationController {

    @Autowired
    TelegramUserRepository telegramUserRepository;

    @Autowired
    NotificationService notificationService;

    @PostMapping(path = "sendMessageToUser", params = {"userId", "message"}, produces = "application/json")
    public ResponseEntity<?> sendMessageToUser(@RequestParam("userId") Long userId,
                                               @RequestParam("message") String message) {
        Optional<TelegramUser> telegramUserOptional = telegramUserRepository.findById(userId);
        if (telegramUserOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }

        return notificationService.sendMessageToUserHandler(userId, message);
    }

    @PostMapping(path = "sendMessageToAllUsers", params = {"message"}, produces = "application/json")
    public ResponseEntity<?> sendMessageToAllUsers(@RequestParam("message") String message) {
        ArrayList<TelegramUser> telegramUsers = (ArrayList<TelegramUser>) telegramUserRepository.findAll();

        for (TelegramUser user : telegramUsers) {
            notificationService.sendMessageToUserHandler(user.getId(), message);
        }

        return ResponseEntity.status(HttpStatus.OK).body("successfully send messages");
    }

}
