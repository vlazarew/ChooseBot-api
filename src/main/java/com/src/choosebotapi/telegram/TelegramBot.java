package com.src.choosebotapi.telegram;

import com.src.choosebotapi.service.TelegramUpdateService;
import com.src.choosebotapi.telegram.utils.handler.TelegramMessageHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource("classpath:telegram.properties")
public class TelegramBot extends TelegramLongPollingBot {

    @Getter
    @Value("${bot.name}")
    String botName;

    @Getter
    @Value("${bot.token}")
    String botToken;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    final TelegramUpdateService telegramUpdateService;
    final List<TelegramMessageHandler> telegramMessageHandlers;

    @Autowired
    public TelegramBot(TelegramUpdateService telegramUpdateService,
                       @Lazy List<TelegramMessageHandler> telegramMessageHandlers) {
        this.telegramUpdateService = telegramUpdateService;
        this.telegramMessageHandlers = telegramMessageHandlers;
    }

    @Override
    public void onUpdateReceived(Update update) {
        CompletableFuture.runAsync(() -> handleUpdate(update));
    }

    private void handleUpdate(Update update) {
        Message message = update.getMessage();
        boolean hasContact = message.hasContact();
        boolean hasText = message.hasText();
        boolean hasLocation = message.hasLocation();

        telegramUpdateService.save(update, message, hasContact, hasLocation)
                .thenApplyAsync(tgUpdate -> {
                    telegramMessageHandlers.forEach(telegramMessageHandler ->
                            CompletableFuture.runAsync(() -> telegramMessageHandler.handle(tgUpdate, hasText,
                                    hasContact, hasLocation)));
                    return null;
                });
    }
}
