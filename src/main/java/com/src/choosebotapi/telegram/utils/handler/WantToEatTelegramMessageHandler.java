package com.src.choosebotapi.telegram.utils.handler;

import com.src.choosebotapi.data.model.restaurant.*;
import com.src.choosebotapi.data.model.telegram.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.src.choosebotapi.data.model.telegram.UserStatus.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@EnableAsync
@PropertySource("classpath:ui.properties")
@Log4j2
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

    @Value("${telegram.selectDishesFromTop}")
    String selectDishesFromTop;

    @Value("${telegram.selectBookOrRoute}")
    String selectBookOrRoute;

    @Value("${telegram.bookRestaurantNotImplemented}")
    String bookRestaurantNotImplemented;

    @Value("${telegram.wantToEat}")
    String wantToEat;

    @Value("${telegram.routeToRestaurant}")
    String routeToRestaurant;

    @Override
    @Async
    public void handle(TelegramUpdate telegramUpdate, boolean hasText, boolean hasContact, boolean hasLocation) {
        TelegramMessage telegramMessage = telegramUpdate.getMessage();
        String messageText = telegramMessage.getText();
        TelegramUser telegramUser = telegramMessage.getFrom();
        UserStatus status = telegramUser.getStatus();
        Long chatId = telegramMessage.getChat().getId();

        if (hasLocation) {
            handleEnterLocation(chatId);
        }

        if (!hasText) {
            return;
        }


        if (status == WantToEat && messageText.startsWith(WANT_TO_EAT)) {
            handleWantToEat(chatId);
        } else if (status == EnterDishOrGetRecommendations) {
            handleEnterDishOrGetRecommendations(chatId, messageText);
        } else if (status == EnterDishName) {
            handleEnterDishName(chatId, messageText, telegramUser);
        } else if (status == SelectAverageCheck) {
            handleSelectAverageCheck(chatId, telegramUser, messageText);
        } else if (status == SelectDishCategory) {
            handleSelectDishCategory(chatId, messageText, telegramUser);
        } else if (status == SelectDishKitchenDirection) {
            handleSelectDishKitchenDirection(chatId, messageText, telegramUser);
        } else if (status == GetResultTopDishesByCategory) {
            handleResultTopDishes(chatId, messageText, telegramUser);
        } else if (status == SelectBookOrRoute) {
            handleSelectBookOrRoute(chatId, messageText, telegramUser);
        }

    }

    @Async
    void handleSelectBookOrRoute(Long chatId, String messageText, TelegramUser telegramUser) {
        Session session = sessionRepository.findByUser_IdAndNotificationSendAndSessionFinished(telegramUser.getId(), false, false);
        if (Objects.equals(messageText, BOOK_PLACE_IN_RESTAURANT)) {
            sendSelectBookOrRoute(chatId, bookRestaurantNotImplemented, null);
        } else if (Objects.equals(messageText, MAKE_ROUTE_TO_RESTAURANT)) {
            session.setSessionFinished(true);
            sessionRepository.save(session);

            sendTextMessageWithoutKeyboard(chatId, "Спасибо, что воспользовались нашим сервисом подбора блюд ChooseEat. " +
                    "Ссылка на маршрут будет выслана в течении нескольких секунд", null);

            URI routeUrl = yandexGeoDecoderService.makeRouteUrl(session);

            sendTextMessageWithoutKeyboard(chatId, routeToRestaurant + "\n\n" + routeUrl.toString(), null);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e);
            }
            sendMessageWantToEat(chatId, wantToEat, WantToEat);
        }
    }


    @Async
    void handleResultTopDishes(Long chatId, String messageText, TelegramUser telegramUser) {
        Session session = sessionRepository.findByUser_IdAndNotificationSendAndSessionFinished(telegramUser.getId(), false, false);
        int listLength = session.getDishesToSelect().size();
        int newIndex = 0;
        String message = "Блюдо не найдено";

        if (messageText.equals(EXIT_DISH)) {
            session.setDish(null);
            session.setDishesToSelect(null);
            session.setDishIndexInList(0);
            session.setDishKitchenDirection(null);
            session.setDishCategory(null);
            session.setDishTemplate(null);
            session.setAverageCheck(null);
            sessionRepository.save(session);
            sendMessageEnterDishOrGetRecommendations(chatId, enterDishOrGetRecommendations, EnterDishOrGetRecommendations);
            return;
        } else if (Objects.equals(messageText, PREVIOUS_DISH)) {
            newIndex = session.getDishIndexInList() == 0 ? listLength - 1 : session.getDishIndexInList() - 1;
            message = "Предыдущее блюдо";
        } else if (Objects.equals(messageText, NEXT_DISH)) {
            newIndex = session.getDishIndexInList() == listLength - 1 ? 0 : session.getDishIndexInList() + 1;
            message = "Следующее блюдо";
        } else if (messageText.equals(SELECT_DISH)) {
            Dish dish = session.getDishesToSelect().get(session.getDishIndexInList());
            session.setDish(dish);
            sessionRepository.save(session);
            sendSelectBookOrRoute(chatId, selectBookOrRoute, SelectBookOrRoute);
            return;
        }

        session.setDishIndexInList(newIndex);
        sessionRepository.save(session);

        sendSelectDishFromTop(chatId, message, GetResultTopDishesByCategory);
    }

    @Async
    void handleSelectDishCategory(Long chatId, String messageText, TelegramUser telegramUser) {
        Session session = sessionRepository.findByUser_IdAndNotificationSendAndSessionFinished(telegramUser.getId(), false, false);
        Optional<DishCategory> category = dishCategoryRepository.findByName(messageText);

        if (category.isEmpty()) {
            sendSelectDishCategory(chatId, "Блюд по выбранной категории не найдено", SelectDishCategory);
            return;
        }

        session.setDishCategory(category.get());
        sessionRepository.save(session);
        sendSelectDishKitchenDirection(chatId, selectDishKitchenDirection, SelectDishKitchenDirection);
    }

    @Async
    void handleSelectDishKitchenDirection(Long chatId, String messageText, TelegramUser telegramUser) {
        Session session = sessionRepository.findByUser_IdAndNotificationSendAndSessionFinished(telegramUser.getId(), false, false);
        Optional<DishKitchenDirection> kitchenDirection = dishKitchenDirectionRepository.findByName(messageText);

        if (kitchenDirection.isEmpty()) {
            sendSelectDishKitchenDirection(chatId, "Блюд по выбранной кухне не найдено", SelectDishKitchenDirection);
            return;
        }

        session.setDishKitchenDirection(kitchenDirection.get());
        sessionRepository.save(session);
        sendSelectAverageCheck(chatId, selectAverageCheck, SelectAverageCheck);
    }

    @Async
    void handleWantToEat(Long chatId) {
        sendMessageShareLocation(chatId, shareLocation, EnterLocation);
    }

    @Async
    void handleEnterLocation(Long chatId) {
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
    void handleEnterDishName(Long chatId, String text, TelegramUser telegramUser) {
        Session session = sessionRepository.findByUser_IdAndNotificationSendAndSessionFinished(telegramUser.getId(), false, false);
        session.setDishTemplate(text);
        sessionRepository.save(session);
        sendSelectAverageCheck(chatId, selectAverageCheck, SelectAverageCheck);
    }

    @Async
    void handleSelectAverageCheck(Long chatId, TelegramUser telegramUser, String text) {
        Session session = sessionRepository.findByUser_IdAndNotificationSendAndSessionFinished(telegramUser.getId(), false, false);

        String dishTemplate = session.getDishTemplate() == null ? "" : session.getDishTemplate();
        Long dishCategory = (session.getDishCategory() != null && session.getDishCategory().getId() != null) ? session.getDishCategory().getId() : null;
        Long dishKitchenDirection = (session.getDishKitchenDirection() != null && session.getDishKitchenDirection().getId() != null) ? session.getDishKitchenDirection().getId() : null;
        TelegramLocation location = session.getLocation();

        List<Object> dishObjectList = dishRepository.findTop10ByRating(dishTemplate, text, dishCategory, dishKitchenDirection,
                location.getLatitude(), location.getLongitude(), 3000F);
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

        session.setDishesToSelect(dishes);
        session.setAverageCheck(text);
        sessionRepository.save(session);

        String message = getMessage(session);

        sendSelectDishFromTop(chatId, message, GetResultTopDishesByCategory);
    }

    private String getMessage(Session session) {
        String dishTemplate = session.getDishTemplate() == null ? "" : "*Блюдо*: " + session.getDishTemplate() + "\n";
        String averageCheck = session.getAverageCheck() == null ? "" : "*Средний чек*: " + session.getAverageCheck() + "\n";

        String dishCategory = "";
        if (session.getDishCategory() != null && session.getDishCategory().getName() != null) {
            dishCategory = "*Категория*: " + session.getDishCategory().getName() + "\n";
        }

        String dishKitchenDirection = "";
        if (session.getDishKitchenDirection() != null && session.getDishKitchenDirection().getName() != null) {
            dishKitchenDirection = "*Кухня*: " + session.getDishKitchenDirection().getName() + "\n";
        }

        return selectDishesFromTop + "\n" + dishTemplate + averageCheck + dishCategory + dishKitchenDirection;
    }
}
