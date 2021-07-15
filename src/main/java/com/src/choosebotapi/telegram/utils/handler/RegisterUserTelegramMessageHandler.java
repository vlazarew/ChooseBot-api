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

import static com.src.choosebotapi.data.model.UserStatus.EnterFullName;
import static com.src.choosebotapi.data.model.UserStatus.NotRegistered;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@EnableAsync
@PropertySource("classpath:ui.properties")
public class RegisterUserTelegramMessageHandler extends TelegramHandler {

    @Value("${telegram.enterFullUserName}")
    String enterFullUserNameMessage;

    @Autowired
    TelegramUserRepository telegramUserRepository;

    @Override
    @Async
    public void handle(TelegramUpdate telegramUpdate, boolean hasText, boolean hasContact, boolean hasLocation) {
        if (!hasText) {
            return;
        }

        TelegramMessage telegramMessage = telegramUpdate.getMessage();
        String messageText = telegramMessage.getText();
        TelegramUser telegramUser = telegramMessage.getFrom();
        UserStatus status = telegramUser.getStatus();
        Long chatId = telegramMessage.getChat().getId();

        if (status == NotRegistered) {
            sendMessageToUserByCustomMainKeyboard(chatId, telegramUser, enterFullUserNameMessage, EnterFullName);
        } else if (status == EnterFullName) {
            enterFullUserName(telegramUser, messageText);
        }

    }

    private void enterFullUserName(TelegramUser telegramUser, String messageText) {
        telegramUser.setFullName(messageText.trim());
        telegramUserRepository.save(telegramUser);
    }

}