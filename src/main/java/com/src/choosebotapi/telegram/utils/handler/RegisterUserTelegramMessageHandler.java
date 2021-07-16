package com.src.choosebotapi.telegram.utils.handler;

import com.src.choosebotapi.data.model.TelegramMessage;
import com.src.choosebotapi.data.model.TelegramUpdate;
import com.src.choosebotapi.data.model.TelegramUser;
import com.src.choosebotapi.data.model.UserStatus;
import com.src.choosebotapi.data.repository.TelegramUserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static com.src.choosebotapi.data.model.UserStatus.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@EnableAsync
@PropertySource("classpath:ui.properties")
public class RegisterUserTelegramMessageHandler extends TelegramHandler {

    @Value("${telegram.sharePhoneNumber}")
    String sharePhoneNumber;

    @Value("${telegram.shareLocation}")
    String shareLocation;

    @Autowired
    TelegramUserRepository telegramUserRepository;

    @Override
    @Async
    public void handle(TelegramUpdate telegramUpdate, boolean hasText, boolean hasContact, boolean hasLocation) {
        TelegramMessage telegramMessage = telegramUpdate.getMessage();
        String messageText = telegramMessage.getText();
        TelegramUser telegramUser = telegramMessage.getFrom();
        UserStatus status = telegramUser.getStatus();
        Long chatId = telegramMessage.getChat().getId();

        if (status == EnterFullName) {
            enterFullUserName(telegramUser, messageText, chatId);
        } else if (status == EnterPhone) {
            handleEnterPhone(telegramUser, messageText, chatId);
        }

    }

    @Async
    void handleEnterPhone(TelegramUser telegramUser, String messageText, Long chatId) {
//        if (messageText.startsWith(SHARE_PHONE_NUMBER)) {
//            saveContact()
//        }
        sendMessageShareLocation(chatId, telegramUser, shareLocation, EnterLocation);
    }

    @Async
    void enterFullUserName(TelegramUser telegramUser, String messageText, Long chatId) {
        telegramUser.setFullName(messageText.trim());
        CompletableFuture.completedFuture(telegramUserRepository.save(telegramUser));
        sendMessageVerifyPhoneNumber(chatId, telegramUser, sharePhoneNumber, EnterPhone);
    }

}
