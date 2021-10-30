package com.src.choosebotapi.telegram.utils.handler;

import com.src.choosebotapi.data.model.telegram.TelegramMessage;
import com.src.choosebotapi.data.model.telegram.TelegramUpdate;
import com.src.choosebotapi.service.NotificationService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource("classpath:ui.properties")
public class HelloTelegramMessageHandler extends TelegramHandler {

    @Autowired
    NotificationService notificationService;

    @Override
    @Async
    public void handle(TelegramUpdate telegramUpdate, boolean hasText, boolean hasContact, boolean hasLocation) {
        if (!hasText) {
            return;
        }

        TelegramMessage telegramMessage = telegramUpdate.getMessage();
        String messageText = telegramMessage.getText();

        if (messageText.equals(START_COMMAND)) {
            Long chatId = telegramMessage.getChat().getId();
            sendHelloMessage(chatId);
        } else if (messageText.equals(STATS_COMMAND)) {
            notificationService.sendUserStats();
        }


    }


}
