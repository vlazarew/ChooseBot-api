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

    public CompletableFuture<ReplyKeyboardMarkup> getSharePhoneNumberKeyboardMarkup() {
        CompletableFuture<ReplyKeyboardMarkup> replyKeyboardMarkup =
                CompletableFuture.completedFuture(getTunedReplyKeyboardMarkup());
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardButton sharePhoneButton = new KeyboardButton(telegramHandler.SHARE_PHONE_NUMBER);
        sharePhoneButton.setRequestContact(true);

        keyboardFirstRow.add(sharePhoneButton);
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.SKIP));
        keyboard.add(keyboardFirstRow);

        replyKeyboardMarkup.thenCompose(
                replyKeyboardMarkup1 -> CompletableFuture.runAsync(() -> replyKeyboardMarkup1.setKeyboard(keyboard)));

        return replyKeyboardMarkup;
    }

    public CompletableFuture<ReplyKeyboardMarkup> getShareLocationKeyboardMarkup() {
        CompletableFuture<ReplyKeyboardMarkup> replyKeyboardMarkup =
                CompletableFuture.completedFuture(getTunedReplyKeyboardMarkup());
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardButton shareLocationButton = new KeyboardButton(telegramHandler.SHARE_LOCATION);
        shareLocationButton.setRequestLocation(true);

        keyboardFirstRow.add(shareLocationButton);
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.SKIP));
        keyboard.add(keyboardFirstRow);

        replyKeyboardMarkup.thenCompose(
                replyKeyboardMarkup1 -> CompletableFuture.runAsync(() -> replyKeyboardMarkup1.setKeyboard(keyboard)));

        return replyKeyboardMarkup;
    }

    public CompletableFuture<ReplyKeyboardMarkup> getWantToEatKeyboardMarkup() {
        CompletableFuture<ReplyKeyboardMarkup> replyKeyboardMarkup =
                CompletableFuture.completedFuture(getTunedReplyKeyboardMarkup());
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.WANT_TO_EAT));
        keyboard.add(keyboardFirstRow);

        replyKeyboardMarkup.thenCompose(
                replyKeyboardMarkup1 -> CompletableFuture.runAsync(() -> replyKeyboardMarkup1.setKeyboard(keyboard)));

        return replyKeyboardMarkup;
    }

    public CompletableFuture<ReplyKeyboardMarkup> getEnterDishOrGetRecommendationsKeyboardMarkup() {
        CompletableFuture<ReplyKeyboardMarkup> replyKeyboardMarkup =
                CompletableFuture.completedFuture(getTunedReplyKeyboardMarkup());
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.ENTER_DISH_NAME));
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.GET_DISH_RECOMMENDATION));
        keyboard.add(keyboardFirstRow);

        replyKeyboardMarkup.thenCompose(
                replyKeyboardMarkup1 -> CompletableFuture.runAsync(() -> replyKeyboardMarkup1.setKeyboard(keyboard)));

        return replyKeyboardMarkup;
    }

    public CompletableFuture<ReplyKeyboardMarkup> getSelectAverageCheckKeyboardMarkup() {
        CompletableFuture<ReplyKeyboardMarkup> replyKeyboardMarkup =
                CompletableFuture.completedFuture(getTunedReplyKeyboardMarkup());
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.DOWN_1_5K));
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.FROM_1_5K_TO_2_5K));
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.UPPER_2_5K));
        keyboard.add(keyboardFirstRow);

        replyKeyboardMarkup.thenCompose(
                replyKeyboardMarkup1 -> CompletableFuture.runAsync(() -> replyKeyboardMarkup1.setKeyboard(keyboard)));

        return replyKeyboardMarkup;
    }

    public CompletableFuture<ReplyKeyboardMarkup> getSelectDishDirectionKeyboardMarkup() {
        CompletableFuture<ReplyKeyboardMarkup> replyKeyboardMarkup =
                CompletableFuture.completedFuture(getTunedReplyKeyboardMarkup());
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.VEGAN_DISH_DIRECTION));
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.HEALTHY_DISH_DIRECTION));
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.COMMON_DISH_DIRECTION));
        keyboard.add(keyboardFirstRow);

        replyKeyboardMarkup.thenCompose(
                replyKeyboardMarkup1 -> CompletableFuture.runAsync(() -> replyKeyboardMarkup1.setKeyboard(keyboard)));

        return replyKeyboardMarkup;
    }

    public CompletableFuture<ReplyKeyboardMarkup> getSelectHealthyDishSubDirectionKeyboardMarkup() {
        CompletableFuture<ReplyKeyboardMarkup> replyKeyboardMarkup =
                CompletableFuture.completedFuture(getTunedReplyKeyboardMarkup());
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.HEALTHY_GLUTEN_FREE_DISH_DIRECTION));
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.HEALTHY_LACTOSE_FREE_DISH_DIRECTION));
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.HEALTHY_KETO_RESTAURANT_DISH_DIRECTION));
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
