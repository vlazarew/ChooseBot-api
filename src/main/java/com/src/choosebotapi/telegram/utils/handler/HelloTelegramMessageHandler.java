package com.src.choosebotapi.telegram.utils.handler;

import com.src.choosebotapi.data.model.telegram.TelegramMessage;
import com.src.choosebotapi.data.model.telegram.TelegramUpdate;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static com.src.choosebotapi.data.model.telegram.UserStatus.WantToEat;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource("classpath:ui.properties")
public class HelloTelegramMessageHandler extends TelegramHandler {



    //    @Value("${telegram.enterFullUserName}")
//    String enterFullUserNameMessage;

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

        sendHelloMessage(chatId);
    }



}
