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
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.DOWN_1K));
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.FROM_1K_TO_2K));
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.UPPER_2K));
        keyboard.add(keyboardFirstRow);

        replyKeyboardMarkup.thenCompose(
                replyKeyboardMarkup1 -> CompletableFuture.runAsync(() -> replyKeyboardMarkup1.setKeyboard(keyboard)));

        return replyKeyboardMarkup;
    }

    public CompletableFuture<ReplyKeyboardMarkup> getSelectDishCategoryKeyboardMarkup() {
        CompletableFuture<ReplyKeyboardMarkup> replyKeyboardMarkup =
                CompletableFuture.completedFuture(getTunedReplyKeyboardMarkup());
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.SNACK_CATEGORY));
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.SALAD_CATEGORY));
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.SOUP_CATEGORY));

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton(telegramHandler.HOT_DISHES_CATEGORY));
        keyboardSecondRow.add(new KeyboardButton(telegramHandler.DESSERT_CATEGORY));
        keyboardSecondRow.add(new KeyboardButton(telegramHandler.ALCOHOLIC_DRINKS_CATEGORY));

        KeyboardRow keyboardThirdRow = new KeyboardRow();
        keyboardThirdRow.add(new KeyboardButton(telegramHandler.SOFT_DRINKS_CATEGORY));
        keyboardThirdRow.add(new KeyboardButton(telegramHandler.SUSHI_ROLLS_CATEGORY));
        keyboardThirdRow.add(new KeyboardButton(telegramHandler.BREAKFAST_CATEGORY));

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);

        replyKeyboardMarkup.thenCompose(
                replyKeyboardMarkup1 -> CompletableFuture.runAsync(() -> replyKeyboardMarkup1.setKeyboard(keyboard)));

        return replyKeyboardMarkup;
    }

    public CompletableFuture<ReplyKeyboardMarkup> getSelectDishKitchenDirectionKeyboardMarkup() {
        CompletableFuture<ReplyKeyboardMarkup> replyKeyboardMarkup =
                CompletableFuture.completedFuture(getTunedReplyKeyboardMarkup());
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.EUROPEAN_KITCHEN_DIRECTION));
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.RUSSIAN_KITCHEN_DIRECTION));

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton(telegramHandler.CAUCASIAN_KITCHEN_DIRECTION));
        keyboardSecondRow.add(new KeyboardButton(telegramHandler.ITALIAN_KITCHEN_DIRECTION));

        KeyboardRow keyboardThirdRow = new KeyboardRow();
        keyboardThirdRow.add(new KeyboardButton(telegramHandler.ASIAN_KITCHEN_DIRECTION));

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);

        replyKeyboardMarkup.thenCompose(
                replyKeyboardMarkup1 -> CompletableFuture.runAsync(() -> replyKeyboardMarkup1.setKeyboard(keyboard)));

        return replyKeyboardMarkup;
    }

    public CompletableFuture<ReplyKeyboardMarkup> getSelectDishFromTopKeyboardMarkup() {
        CompletableFuture<ReplyKeyboardMarkup> replyKeyboardMarkup =
                CompletableFuture.completedFuture(getTunedReplyKeyboardMarkup());
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.PREVIOUS_DISH));
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.SELECT_DISH));
        keyboardFirstRow.add(new KeyboardButton(telegramHandler.NEXT_DISH));

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton(telegramHandler.EXIT_DISH));

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
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
