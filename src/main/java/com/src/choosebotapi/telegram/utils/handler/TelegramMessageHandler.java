package com.src.choosebotapi.telegram.utils.handler;

import com.src.choosebotapi.data.model.TelegramUpdate;

public interface TelegramMessageHandler {
    void handle(TelegramUpdate telegramUpdate, boolean hasText, boolean hasContact, boolean hasLocation);
}
