package com.src.choosebotapi.telegram.utils.handler;

import com.src.choosebotapi.data.model.TelegramMessage;
import com.src.choosebotapi.data.model.TelegramUpdate;
import com.src.choosebotapi.data.model.TelegramUser;
import com.src.choosebotapi.data.model.UserStatus;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import static com.src.choosebotapi.data.model.UserStatus.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@EnableAsync
@PropertySource("classpath:ui.properties")
public class WantToEatTelegramMessageHandler extends TelegramHandler {

    @Value("${telegram.enterDishOrGetRecommendations}")
    String enterDishOrGetRecommendations;

    @Value("${telegram.enterDishName}")
    String enterDishName;

    @Value("${telegram.selectAverageCheck}")
    String selectAverageCheck;

    @Value("${telegram.selectDishDirection}")
    String selectDishDirection;

    @Value("${telegram.selectHealthyDishSubDirection}")
    String selectHealthyDishSubDirection;

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

        if (status == WantToEat && messageText.startsWith(WANT_TO_EAT)) {
            handleWantToEat(chatId);
        } else if (status == EnterDishOrGetRecommendations) {
            handleEnterDishOrGetRecommendations(chatId, messageText);
        } else if (status == EnterDishName) {
            handleEnterDishName(chatId);
        } else if (status == SelectAverageCheck) {
            handleSelectAverageCheck(chatId);
        } else if (status == SelectDishDirection || status == SelectHealthyDishSubDirection) {
            handleSelectDishDirection(chatId, messageText);
        }

    }

    @Async
    void handleSelectDishDirection(Long chatId, String messageText) {
        if (messageText.startsWith(HEALTHY_DISH_DIRECTION)) {
            sendSelectHealthyDishSubDirection(chatId, selectHealthyDishSubDirection, SelectHealthyDishSubDirection);
        } else {
//            sendTextMessageWithoutKeyboard(chatId, "Result from database currently not implemented", GetResultRestaurantFromDB);
            sendMessageWantToEat(chatId, "Result from database currently not implemented", WantToEat);
        }
    }

    @Async
    void handleWantToEat(Long chatId) {
        sendMessageEnterDishOrGetRecommendations(chatId, enterDishOrGetRecommendations, EnterDishOrGetRecommendations);
    }

    @Async
    void handleEnterDishOrGetRecommendations(Long chatId, String messageText) {
        if (messageText.startsWith(ENTER_DISH_NAME)) {
            sendTextMessageWithoutKeyboard(chatId, enterDishName, EnterDishName);
        } else if (messageText.startsWith(GET_DISH_RECOMMENDATION)) {
            sendSelectDishDirection(chatId, selectDishDirection, SelectDishDirection);
        }
    }

    @Async
    void handleEnterDishName(Long chatId) {
        sendSelectAverageCheck(chatId, selectAverageCheck, SelectAverageCheck);
    }

    @Async
    void handleSelectAverageCheck(Long chatId) {
//        sendTextMessageWithoutKeyboard(chatId, "Result from database currently not implemented", GetResultRestaurantFromDB);
        sendMessageWantToEat(chatId, "Result from database currently not implemented", WantToEat);
    }
}
