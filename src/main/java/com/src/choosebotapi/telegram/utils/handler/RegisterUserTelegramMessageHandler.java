package com.src.choosebotapi.telegram.utils.handler;

import com.src.choosebotapi.data.model.telegram.TelegramMessage;
import com.src.choosebotapi.data.model.telegram.TelegramUpdate;
import com.src.choosebotapi.data.model.telegram.TelegramUser;
import com.src.choosebotapi.data.model.telegram.UserStatus;
import com.src.choosebotapi.data.repository.telegram.TelegramUserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static com.src.choosebotapi.data.model.telegram.UserStatus.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@EnableAsync
@PropertySource("classpath:ui.properties")
public class RegisterUserTelegramMessageHandler extends TelegramHandler {

    @Value("${telegram.sharePhoneNumber}")
    String sharePhoneNumber;

    @Value("${telegram.wantToEat}")
    String wantToEat;

    @Autowired
    TelegramUserRepository telegramUserRepository;

    @Override
    @Async
    public void handle(TelegramUpdate telegramUpdate, boolean hasText, boolean hasContact, boolean hasLocation) {
        if (!hasText && !hasContact) {
            return;
        }

        TelegramMessage telegramMessage = telegramUpdate.getMessage();
        String messageText = telegramMessage.getText();
        TelegramUser telegramUser = telegramMessage.getFrom();
        UserStatus status = telegramUser.getStatus();
        Long chatId = telegramMessage.getChat().getId();

        if (status == EnterFullName) {
            enterFullUserName(telegramUser, messageText, chatId);
        } else if (status == EnterPhone) {
            handleEnterPhone(chatId);
        }

    }

    @Async
    void handleEnterPhone(Long chatId) {
        sendMessageWantToEat(chatId, wantToEat, WantToEat);
    }

    @Async
    void enterFullUserName(TelegramUser telegramUser, String messageText, Long chatId) {
        telegramUser.setFullName(messageText.trim());
        CompletableFuture.completedFuture(telegramUserRepository.save(telegramUser));
        sendMessageVerifyPhoneNumber(chatId, sharePhoneNumber, EnterPhone);
    }

}
