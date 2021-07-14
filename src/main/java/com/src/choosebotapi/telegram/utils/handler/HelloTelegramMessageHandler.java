package com.src.choosebotapi.telegram.utils.handler;

import com.src.choosebotapi.data.model.TelegramMessage;
import com.src.choosebotapi.data.model.TelegramUpdate;
import com.src.choosebotapi.data.model.TelegramUser;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@EnableAsync
@PropertySource("classpath:ui.properties")
public class HelloTelegramMessageHandler extends TelegramHandler {

    @Value("${telegram.helloMessage}")
    String helloMessage;

    @Override
    @Async
    public void handle(TelegramUpdate telegramUpdate, boolean hasText, boolean hasContact, boolean hasLocation) {
        if (!hasText) {
            return;
        }

        TelegramMessage telegramMessage = telegramUpdate.getMessage();
        String messageText = telegramMessage.getText();

        if (!messageText.startsWith(START_COMMAND)
                && !messageText.equals(HELLO_BUTTON)) {
            return;
        }

        Long chatId = telegramMessage.getChat().getId();
        TelegramUser telegramUser = telegramMessage.getFrom();

        sendMessageToUserByCustomMainKeyboard(chatId, telegramUser, helloMessage, null);
    }

}
