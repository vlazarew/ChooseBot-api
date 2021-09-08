package com.src.choosebotapi.controller;

import com.src.choosebotapi.data.model.telegram.TelegramUser;
import com.src.choosebotapi.data.repository.telegram.TelegramUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RequestMapping(value = "/notification", produces = "application/json")
@CrossOrigin("*")
@PropertySource("classpath:telegram.properties")
public class NotificationController {

    @Autowired
    TelegramUserRepository telegramUserRepository;

    @Value("${notification.sendMessageTemplate}")
    String messageTemplate;

    @Value("${bot.token}")
    String botToken;

    @PostMapping(path = "sendMessageToUser", params = {"userId", "message"}, produces = "application/json")
    public ResponseEntity<?> setNewStatusForUser(@RequestParam("userId") Long userId,
                                              @RequestParam("message") String message) throws IOException {
        Optional<TelegramUser> telegramUserOptional = telegramUserRepository.findById(userId);
        if (telegramUserOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }

        return sendMessageToUser(userId, message);
    }

    @PostMapping(path = "sendMessageToAllUsers", params = {"message"}, produces = "application/json")
    public ResponseEntity<?> setNewStatusForUser(@RequestParam("message") String message) throws IOException {
        ArrayList<TelegramUser> telegramUsers = (ArrayList<TelegramUser>) telegramUserRepository.findAll();

        for (TelegramUser user : telegramUsers) {
            if (sendMessageToUser(user.getId(), message).getStatusCode() != HttpStatus.OK){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("errors with sending messages");
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body("successfully send messages");
    }

    private ResponseEntity<?> sendMessageToUser(Long userId, String message) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = getTemplate(userId, message);

        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                return ResponseEntity.status(statusCode).body(response.getEntity().getContent());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body("successfully send messages");
    }

    private HttpGet getTemplate(Long chatId, String message) {
        UriTemplate template = new UriTemplate(messageTemplate);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("botToken", botToken);
        parameters.put("chatId", chatId.toString());
        parameters.put("message", message);
        return new HttpGet(template.expand(parameters));
    }

}
