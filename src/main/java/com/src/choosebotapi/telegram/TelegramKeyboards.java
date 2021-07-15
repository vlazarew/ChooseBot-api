package com.src.choosebotapi.telegram;

import com.src.choosebotapi.data.model.TelegramUser;
import com.src.choosebotapi.telegram.utils.handler.TelegramHandler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class TelegramKeyboards {

    @Autowired
    TelegramHandler telegramHandler;

    public CompletableFuture<ReplyKeyboardMarkup> getCustomReplyMainKeyboardMarkup(TelegramUser user) {
        CompletableFuture<ReplyKeyboardMarkup> replyKeyboardMarkup = CompletableFuture.completedFuture(getTunedReplyKeyboardMarkup());

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

//        keyboardFirstRow.add(new KeyboardButton(telegramHandler.SHARE_PHONE_NUMBER));
//        keyboardFirstRow.add(new KeyboardButton(telegramHandler.SKIP));
//        keyboardFirstRow.add(new KeyboardButton(telegramHandler.TWITTER_BUTTON));
//        keyboardFirstRow.add(new KeyboardButton(telegramHandler.WEATHER_BUTTON));

        keyboard.add(keyboardFirstRow);

        replyKeyboardMarkup.thenCompose(
                replyKeyboardMarkup1 -> CompletableFuture.runAsync(() -> replyKeyboardMarkup1.setKeyboard(keyboard)));
        return replyKeyboardMarkup;
    }

    public CompletableFuture<ReplyKeyboardMarkup> getConfirmFullNameToOrderKeyboardMarkup() {
        CompletableFuture<ReplyKeyboardMarkup> replyKeyboardMarkup =
                CompletableFuture.completedFuture(getTunedReplyKeyboardMarkup());
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.CONFIRM_FULLNAME_FOR_ORDER));
        keyboard.add(keyboardFirstRow);

        replyKeyboardMarkup.thenCompose(
                replyKeyboardMarkup1 -> CompletableFuture.runAsync(() -> replyKeyboardMarkup1.setKeyboard(keyboard)));

        return replyKeyboardMarkup;
    }

    private ReplyKeyboardMarkup getTunedReplyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        return replyKeyboardMarkup;
    }

}
