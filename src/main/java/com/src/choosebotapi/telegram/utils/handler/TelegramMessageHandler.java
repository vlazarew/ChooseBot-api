package com.src.choosebotapi.telegram.utils.handler;

import com.src.choosebotapi.data.model.telegram.TelegramUpdate;
import com.src.choosebotapi.data.model.telegram.TelegramUser;
import com.src.choosebotapi.data.model.telegram.UserStatus;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public interface TelegramMessageHandler {
    void handle(TelegramUpdate telegramUpdate, boolean hasText, boolean hasContact, boolean hasLocation);

    void sendTextMessageReplyKeyboardMarkup(Long chatId, String text, ReplyKeyboardMarkup replyKeyboardMarkup,
                                            UserStatus status);

    void sendTextMessageWithoutKeyboard(Long chatId, String text, UserStatus status);

    void sendMessageToUserByCustomMainKeyboard(Long chatId, TelegramUser telegramUser, String text, UserStatus status);
}
