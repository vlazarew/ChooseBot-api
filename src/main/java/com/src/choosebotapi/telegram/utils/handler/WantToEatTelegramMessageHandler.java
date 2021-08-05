package com.src.choosebotapi.telegram.utils.handler;

import com.src.choosebotapi.data.model.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    @Value("${telegram.selectDishCategory}")
    String selectDishCategory;

    @Value("${telegram.selectDishKitchenDirection}")
    String selectDishKitchenDirection;

    @Value("${telegram.shareLocation}")
    String shareLocation;

    @Override
    @Async
    public void handle(TelegramUpdate telegramUpdate, boolean hasText, boolean hasContact, boolean hasLocation) {
        TelegramMessage telegramMessage = telegramUpdate.getMessage();
        String messageText = telegramMessage.getText();
        TelegramUser telegramUser = telegramMessage.getFrom();
        UserStatus status = telegramUser.getStatus();
        Long chatId = telegramMessage.getChat().getId();

        if (hasLocation) {
            handleEnterLocation(chatId, telegramUser);
        }

        if (!hasText) {
            return;
        }


        if (status == WantToEat && messageText.startsWith(WANT_TO_EAT)) {
            handleWantToEat(chatId);
        } else if (status == EnterDishOrGetRecommendations) {
            handleEnterDishOrGetRecommendations(chatId, messageText);
        } else if (status == EnterDishName) {
            handleEnterDishName(chatId);
        } else if (status == SelectAverageCheck) {
            handleSelectAverageCheck(chatId, telegramUser, messageText);
        } else if (status == SelectDishCategory) {
            handleSelectDishCategory(chatId, messageText);
        } else if (status == SelectDishKitchenDirection) {
            handleSelectDishKitchenDirection(chatId, messageText);
        }

    }

    @Async
    void handleSelectDishCategory(Long chatId, String messageText) {
        sendSelectDishKitchenDirection(chatId, selectDishKitchenDirection, SelectDishKitchenDirection);
    }

    @Async
    void handleSelectDishKitchenDirection(Long chatId, String messageText) {
        sendMessageWantToEat(chatId, "Choosed: " + messageText + ". Result from database currently not implemented", WantToEat);
    }

    @Async
    void handleWantToEat(Long chatId) {
        sendMessageShareLocation(chatId, shareLocation, EnterLocation);
    }

    @Async
    void handleEnterLocation(Long chatId, TelegramUser telegramUser) {
        Session session = sessionRepository.findByUser_IdAndNotificationSendAndSessionFinished(telegramUser.getId(), false, false);
        sendMessageEnterDishOrGetRecommendations(chatId, enterDishOrGetRecommendations, EnterDishOrGetRecommendations);
    }

    @Async
    void handleEnterDishOrGetRecommendations(Long chatId, String messageText) {
        if (messageText.startsWith(ENTER_DISH_NAME)) {
            sendTextMessageWithoutKeyboard(chatId, enterDishName, EnterDishName);
        } else if (messageText.startsWith(GET_DISH_RECOMMENDATION)) {
            sendSelectDishCategory(chatId, selectDishCategory, SelectDishCategory);
        }
    }

    @Async
    void handleEnterDishName(Long chatId) {
        sendSelectAverageCheck(chatId, selectAverageCheck, SelectAverageCheck);
    }

    @Async
    void handleSelectAverageCheck(Long chatId, TelegramUser telegramUser, String text) {
//        sendTextMessageWithoutKeyboard(chatId, "Result from database currently not implemented", GetResultRestaurantFromDB);
        sendMessageWantToEat(chatId, "Result from database currently not implemented", WantToEat);
        List<Object> dishObjectList = dishRepository.findTop10ByRating();
        List<DishReviewStats> dishReviewStats = new ArrayList<>();
        List<Dish> dishes = new ArrayList<>();
        for (Object dishObject : dishObjectList) {
            Object[] result = (Object[]) dishObject;
            DishReviewStats stats = new DishReviewStats();
            stats.setSummary(Long.valueOf(result[0].toString()));
            stats.setCount(Long.valueOf(result[1].toString()));
            Dish dish = (dishRepository.findById(Long.valueOf(result[2].toString()))).get();
            stats.setDish(dish);
            dishReviewStats.add(stats);
            dishes.add(dish);
        }

        Session session = sessionRepository.findByUser_IdAndNotificationSendAndSessionFinished(telegramUser.getId(), false, false);
        session.setDishesToSelect(dishes);
        session.setAverageCheck(text);
        sessionRepository.save(session);
        int i = 1;


    }
}
