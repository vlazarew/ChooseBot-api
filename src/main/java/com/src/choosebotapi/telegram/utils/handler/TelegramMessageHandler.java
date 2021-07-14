package com.src.choosebotapi.telegram.utils.handler;

import com.src.choosebotapi.data.model.TelegramUpdate;
import com.src.choosebotapi.data.model.TelegramUser;
import com.src.choosebotapi.data.model.UserStatus;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public interface TelegramMessageHandler {
    void handle(TelegramUpdate telegramUpdate, boolean hasText, boolean hasContact, boolean hasLocation);
    void sendTextMessageReplyKeyboardMarkup(Long chatId, String text, ReplyKeyboardMarkup replyKeyboardMarkup,
                                            UserStatus status);
    void sendMessageToUserByCustomMainKeyboard(Long chatId, TelegramUser telegramUser, String text, UserStatus status);
}
