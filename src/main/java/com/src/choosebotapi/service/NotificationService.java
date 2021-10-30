package com.src.choosebotapi.service;

import com.src.choosebotapi.data.model.telegram.TelegramMessage;
import com.src.choosebotapi.data.model.telegram.TelegramUser;
import com.src.choosebotapi.data.repository.telegram.TelegramMessageRepository;
import com.src.choosebotapi.data.repository.telegram.TelegramUserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableAsync
@EnableScheduling
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource("classpath:commands.properties")
@PropertySource("classpath:telegram.properties")
@Log4j2
public class NotificationService {

    final private static Long VLAZAREW_ID = 397699952L;
    final private static Long VINOPRIV_ID = 201959262L;
    final private static Long BABAN_ID = 823785080L;
    final private static Long TYAN_ID = 300244369L;

    @Value("${telegram.MAKE_ROUTE_TO_RESTAURANT}")
    public String MAKE_ROUTE_TO_RESTAURANT;

    @Value("${notification.sendMessageTemplate}")
    String messageTemplate;

    @Value("${bot.token}")
    String botToken;

    @Autowired
    TelegramUserRepository telegramUserRepository;

    @Autowired
    TelegramMessageRepository telegramMessageRepository;

    @Scheduled(cron = "0 1/1 * * * *")
    @Async
    public void checkNotification() {
    }

    @Scheduled(cron = "0 0 20 * * *")
    @Async
    public void sendUserStats() {
        ArrayList<TelegramUser> allUsers = (ArrayList<TelegramUser>) telegramUserRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        List<TelegramUser> todayUsers = allUsers.stream().filter(user ->
                user.getCreationDate().isAfter(now.minusDays(1))).collect(Collectors.toList());

        List<TelegramUser> weekUsers = allUsers.stream().filter(user ->
                user.getCreationDate().isAfter(now.minusWeeks(1))).collect(Collectors.toList());

        List<TelegramUser> monthUsers = allUsers.stream().filter(user ->
                user.getCreationDate().isAfter(now.minusMonths(1))).collect(Collectors.toList());

        ArrayList<TelegramMessage> allRouteMessages = telegramMessageRepository.findTelegramMessageByText(MAKE_ROUTE_TO_RESTAURANT);

        List<TelegramMessage> todayRouteMessages = allRouteMessages.stream().filter(message ->
                message.getCreationDate().isAfter(now.minusDays(1))).collect(Collectors.toList());

        List<TelegramMessage> weekRouteMessages = allRouteMessages.stream().filter(message ->
                message.getCreationDate().isAfter(now.minusWeeks(1))).collect(Collectors.toList());

        List<TelegramMessage> monthRouteMessages = allRouteMessages.stream().filter(message ->
                message.getCreationDate().isAfter(now.minusMonths(1))).collect(Collectors.toList());

        String message = "Статистика по пользователям\n" +
                "\nНовых пользователей за последние 24 часа: " + todayUsers.size() +
                "\nНовых пользователей за последнюю неделю: " + weekUsers.size() +
                "\nНовых пользователей за последний месяц: " + monthUsers.size() +
                "\nВсего пользователей: " + allUsers.size() +
                "\n\nСтатистика по прокладке маршрута\n" +
                "\nНовых путей за последние 24 часа: " + todayRouteMessages.size() +
                "\nНовых путей за последнюю неделю: " + weekRouteMessages.size() +
                "\nНовых путей за последний месяц: " + monthRouteMessages.size() +
                "\nВсего путей проложено: " + allRouteMessages.size();

        List<Long> targetIds = Arrays.asList(VLAZAREW_ID, VINOPRIV_ID, BABAN_ID, TYAN_ID);
        for (Long id : targetIds) {
            sendMessageToUserHandler(id, message);
        }
    }

    public ResponseEntity<?> sendMessageToUserHandler(Long userId, String message) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = getTemplate(userId, message);

        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                ResponseEntity<InputStream> body = ResponseEntity.status(statusCode).body(response.getEntity().getContent());
                log.error("error sending message (status != 200) to user: " + userId + ". Error: " + body);
            }
        } catch (Exception e) {
            log.error("error sending message (fatal error) to user: " + userId + ". Error: " + e.getMessage());
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
