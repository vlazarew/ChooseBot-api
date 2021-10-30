package com.src.choosebotapi.telegram.utils.handler;

import com.src.choosebotapi.data.model.restaurant.*;
import com.src.choosebotapi.data.model.telegram.*;
import com.src.choosebotapi.data.repository.restaurant.RestaurantRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.*;

import static com.src.choosebotapi.data.model.telegram.UserStatus.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@EnableAsync
@PropertySource("classpath:ui.properties")
@PropertySource("classpath:yandex.properties")
@Log4j2
public class WantToEatTelegramMessageHandler extends TelegramHandler {

    @Autowired
    RestaurantRepository restaurantRepository;

    @Value("${telegram.enterDishOrGetRecommendations}")
    String enterDishOrGetRecommendations;

    @Value("${telegram.enterDishName}")
    String enterDishName;

    @Value("${telegram.selectAverageCheck}")
    String selectAverageCheck;

    @Value("${telegram.selectAverageCheckRepeat}")
    String selectAverageCheckRepeat;

    @Value("${telegram.selectDishCategory}")
    String selectDishCategory;

    @Value("${telegram.selectDishKitchenDirection}")
    String selectDishKitchenDirection;

    @Value("${telegram.shareLocation}")
    String shareLocation;

    @Value("${telegram.shareLocationRepeat}")
    String shareLocationRepeat;

    @Value("${telegram.selectDishesFromTop}")
    String selectDishesFromTop;

    @Value("${telegram.selectBookOrRoute}")
    String selectBookOrRoute;

    @Value("${telegram.bookRestaurantNotImplemented}")
    String bookRestaurantNotImplemented;

//    @Value("${telegram.wantToEat}")
//    String wantToEat;

    @Value("${telegram.wantToEatRepeat}")
    String wantToEatRepeat;

    @Value("${telegram.routeToRestaurant}")
    String routeToRestaurant;

    @Value("${telegram.yandexRouteSuccess}")
    String yandexRouteSuccess;

    @Value("${telegram.dishNotFound}")
    String dishNotFound;

    @Value("${telegram.nextDish}")
    String nextDish;

    @Value("${telegram.dishByKitchenDirectoryNotFound}")
    String dishByKitchenDirectoryNotFound;

    @Value("${telegram.selectDishKitchenDirectionClosestError}")
    String selectDishKitchenDirectionClosestError;

    @Value("${telegram.restaurantByAverageCheckNotFound}")
    String restaurantByAverageCheckNotFound;

    @Value("${telegram.previousDish}")
    String previousDish;

    @Value("${telegram.dishByCategoryNotFound}")
    String dishByCategoryNotFound;

    @Value("${telegram.selectDishCategoryClosestError}")
    String selectDishCategoryClosestError;

    @Value("${telegram.dishDescription}")
    String dishDescription;

    @Value("${telegram.dishName}")
    String dishName;

    @Value("${telegram.dishPrice}")
    String dishPrice;

    @Value("${telegram.dishCategory}")
    String dishCategory;

    @Value("${telegram.dishKitchenDirection}")
    String dishKitchenDirection;

    @Value("${telegram.restaurantAverageCheck}")
    String restaurantAverageCheck;

    @Value("${telegram.dishByNameNotFound}")
    String dishByNameNotFound;

    @Value("${telegram.selectDishesHelloMessage}")
    String selectDishesHelloMessage;

    @Value("${yandex.maxDistance}")
    Float maxDistance;

    @Override
    @Async
    public void handle(TelegramUpdate telegramUpdate, boolean hasText, boolean hasContact, boolean hasLocation) {
        TelegramMessage telegramMessage = telegramUpdate.getMessage();
        String messageText = telegramMessage.getText();
        TelegramUser telegramUser = telegramMessage.getFrom();
        UserStatus status = telegramUser.getStatus();
        Long chatId = telegramMessage.getChat().getId();

        if (hasLocation) {
            TelegramLocation location = telegramMessage.getLocation();
            handleEnterLocation(chatId, location.getLatitude(), location.getLongitude());
        }

        if (!hasText) {
            return;
        }

        if (messageText.equals(START_COMMAND) || messageText.equals(STATS_COMMAND)) {
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
        } else if (status == SelectDishKitchenDirection) {
            handleSelectDishKitchenDirection(chatId, messageText, telegramUser);
        } else if (status == SelectDishCategory) {
            handleSelectDishCategory(chatId, messageText, telegramUser);
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

            sendTextMessageWithoutKeyboard(chatId, yandexRouteSuccess, null);

            URI routeUrl = yandexGeoDecoderService.makeRouteUrl(session);

            sendTextMessageWithoutKeyboard(chatId, routeToRestaurant + "\n\n" + routeUrl.toString(), null);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e);
            }
            sendMessageWantToEat(chatId, wantToEatRepeat, WantToEat);
        }
    }


    @Async
    void handleResultTopDishes(Long chatId, String messageText, TelegramUser telegramUser) {
        Session session = sessionRepository.findByUser_IdAndNotificationSendAndSessionFinished(telegramUser.getId(), false, false);
        int listLength = session.getDishesToSelect().size();
        int newIndex = 0;
        String message = dishNotFound;

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
            message = previousDish;
        } else if (Objects.equals(messageText, NEXT_DISH)) {
            newIndex = session.getDishIndexInList() == listLength - 1 ? 0 : session.getDishIndexInList() + 1;
            message = nextDish;
        } else if (messageText.equals(SELECT_DISH)) {
            Dish dish = getCurrentDish(session);
            session.setDish(dish);
            sessionRepository.save(session);
            sendSelectBookOrRoute(chatId, selectBookOrRoute, SelectBookOrRoute);
            return;
        }

        session.setDishIndexInList(newIndex);
        sessionRepository.save(session);

        Dish dishToPresent = getDishToPresent(telegramUser);
        String dishDetailedMessage = getDishDetailedMessage(dishToPresent);

        sendTextMessageWithoutKeyboard(chatId, message, null);
        sendSelectDishFromTop(chatId, dishDetailedMessage, getCurrentDish(session), GetResultTopDishesByCategory);
    }

    private Dish getCurrentDish(Session session) {
        try {
            return session.getDishesToSelect().get(session.getDishIndexInList());
        } catch (Exception e) {
            return null;
        }
    }

    @Async
    void handleSelectDishKitchenDirection(Long chatId, String messageText, TelegramUser telegramUser) {
        Session session = sessionRepository.findByUser_IdAndNotificationSendAndSessionFinished(telegramUser.getId(), false, false);
        Optional<DishKitchenDirection> kitchenDirection = dishKitchenDirectionRepository.findByName(messageText);

        if (kitchenDirection.isEmpty()) {
            sendSelectDishKitchenDirection(chatId, dishByKitchenDirectoryNotFound, SelectDishKitchenDirection);
            return;
        }

        DishKitchenDirection dishKitchenDirection = kitchenDirection.get();
        List<Object> closestDishes = dishRepository.findTop10ByRating(session.getDishTemplate(), null, null, dishKitchenDirection.getId(),
                session.getLocation().getLatitude(), session.getLocation().getLongitude(), maxDistance);

        if (closestDishes.isEmpty()) {
            sendSelectDishKitchenDirection(chatId, selectDishKitchenDirectionClosestError, SelectDishKitchenDirection);
            return;
        }

        session.setDishKitchenDirection(dishKitchenDirection);
        sessionRepository.save(session);
        sendSelectDishCategory(chatId, selectDishCategory, SelectDishCategory);
    }

    @Async
    void handleSelectDishCategory(Long chatId, String messageText, TelegramUser telegramUser) {
        Session session = sessionRepository.findByUser_IdAndNotificationSendAndSessionFinished(telegramUser.getId(), false, false);
        Optional<DishCategory> category = dishCategoryRepository.findByName(messageText);

        if (category.isEmpty()) {
            sendSelectDishCategory(chatId, dishByCategoryNotFound, SelectDishCategory);
            return;
        }

        DishCategory dishCategory = category.get();
        List<Object> closestDishes = dishRepository.findTop10ByRating(session.getDishTemplate(), null, dishCategory.getId(),
                session.getDishKitchenDirection().getId(), session.getLocation().getLatitude(), session.getLocation().getLongitude(), maxDistance);

        if (closestDishes.isEmpty()) {
            sendSelectDishCategory(chatId, selectDishCategoryClosestError, SelectDishCategory);
            return;
        }

        session.setDishCategory(dishCategory);
        sessionRepository.save(session);
        sendSelectAverageCheck(chatId, selectAverageCheck, SelectAverageCheck);
    }

    @Async
    void handleWantToEat(Long chatId) {
        sendMessageShareLocation(chatId, shareLocation, EnterLocation);
    }

    @Async
    void handleEnterLocation(Long chatId, Float latitude, Float longitude) {
        List<Restaurant> restaurantsAround = restaurantRepository.findClosestRestaurant(latitude, longitude, maxDistance);

        if (restaurantsAround.size() > 0) {
            sendMessageEnterDishOrGetRecommendations(chatId, enterDishOrGetRecommendations, EnterDishOrGetRecommendations);
        } else {
            sendMessageWantToEat(chatId, shareLocationRepeat, WantToEat);
        }
    }

    @Async
    void handleEnterDishOrGetRecommendations(Long chatId, String messageText) {
        if (messageText.startsWith(ENTER_DISH_NAME)) {
            sendTextMessageWithoutKeyboard(chatId, enterDishName, EnterDishName);
        } else if (messageText.startsWith(GET_DISH_RECOMMENDATION)) {
            sendSelectDishKitchenDirection(chatId, selectDishKitchenDirection, SelectDishKitchenDirection);
        }
    }

    @Async
    void handleEnterDishName(Long chatId, String text, TelegramUser telegramUser) {
        List<Dish> dishByNameIsLike = dishRepository.findByNameTemplate(text.toLowerCase(Locale.ROOT));

        if (dishByNameIsLike.size() > 0) {
            Session session = sessionRepository.findByUser_IdAndNotificationSendAndSessionFinished(telegramUser.getId(), false, false);
            session.setDishTemplate(text);
            sessionRepository.save(session);
            sendSelectAverageCheck(chatId, selectAverageCheck, SelectAverageCheck);
        } else {
            sendTextMessageWithoutKeyboard(chatId, dishByNameNotFound, EnterDishName);
        }
    }

    @Async
    void handleSelectAverageCheck(Long chatId, TelegramUser telegramUser, String text) {
        if (!text.equals(DOWN_1K) && !text.equals(FROM_1K_TO_2K) && !text.equals(UPPER_2K)) {
            sendSelectAverageCheck(chatId, selectAverageCheckRepeat, SelectAverageCheck);
        }

        Session session = sessionRepository.findByUser_IdAndNotificationSendAndSessionFinished(telegramUser.getId(), false, false);

        String dishTemplate = session.getDishTemplate() == null ? "" : session.getDishTemplate();
        Long dishCategory = (session.getDishCategory() != null && session.getDishCategory().getId() != null) ? session.getDishCategory().getId() : null;
        Long dishKitchenDirection = (session.getDishKitchenDirection() != null && session.getDishKitchenDirection().getId() != null) ? session.getDishKitchenDirection().getId() : null;
        TelegramLocation location = session.getLocation();

        List<Object> dishObjectList = dishRepository.findTop10ByRating(dishTemplate, text, dishCategory, dishKitchenDirection,
                location.getLatitude(), location.getLongitude(), maxDistance);

        if (dishObjectList.isEmpty()) {
            sendSelectAverageCheck(chatId, restaurantByAverageCheckNotFound, SelectAverageCheck);
            return;
        }

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

        Dish dishToPresent = getDishToPresent(telegramUser);
        String searchDishMessage = getSearchDishMessage(session);
        String dishDetailedMessage = getDishDetailedMessage(dishToPresent);

        sendTextMessageWithoutKeyboard(chatId, selectDishesHelloMessage, null);
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        sendTextMessageWithoutKeyboard(chatId, searchDishMessage, null);
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        sendSelectDishFromTop(chatId, dishDetailedMessage, dishToPresent, GetResultTopDishesByCategory);
    }

    private String getSearchDishMessage(Session session) {
        String dishTemplate = session.getDishTemplate() == null ? "" : makeStringBold(dishName) + session.getDishTemplate() + "\n";
        String averageCheck = session.getAverageCheck() == null ? "" : makeStringBold(restaurantAverageCheck) + session.getAverageCheck() + "\n";

        String dishCategory = "";
        if (session.getDishCategory() != null && session.getDishCategory().getName() != null) {
            dishCategory = makeStringBold(dishCategory) + session.getDishCategory().getName() + "\n";
        }

        String dishKitchenDirection = "";
        if (session.getDishKitchenDirection() != null && session.getDishKitchenDirection().getName() != null) {
            dishKitchenDirection = makeStringBold(dishKitchenDirection) + session.getDishKitchenDirection().getName() + "\n";
        }

        return selectDishesFromTop + "\n" + dishTemplate + averageCheck + dishCategory + dishKitchenDirection;
    }

    String getDishDetailedMessage(Dish dishToPresent) {
        Restaurant restaurant = dishToPresent.getRestaurant();

        String mainInfo = "" + dishToPresent.getName() + " / " + restaurant.getName() + " / " +
                restaurant.getAverageCheck() + " / " + restaurant.getAddress();
        String descriptionRaw = dishToPresent.getDescription();
        String description = (descriptionRaw == null || descriptionRaw.equals("")) ? "" : dishDescription + descriptionRaw + "\n";

        Float priceRaw = dishToPresent.getPrice();
        String price = priceRaw == null ? "" : dishPrice + Math.round(priceRaw) + "\n";

        String kitchenName = dishToPresent.getKitchenDirection().getName();
        String kitchen = (kitchenName == null || kitchenName.equals("")) ? "" : dishKitchenDirection + kitchenName;

        String categoryName = dishToPresent.getCategory().getName();
        String category = (categoryName == null || categoryName.equals("")) ? "" : dishCategory + categoryName + "\n";

        boolean hasAdditional = (!description.equals("")) || (!price.equals("")) || (!category.equals("")) || (!kitchen.equals(""));
        return mainInfo + (hasAdditional ? "\n\n" + description + price + category + kitchen : "");
    }

    private Dish getDishToPresent(TelegramUser user) {
        Session currentSession = sessionRepository.findByUser_IdAndNotificationSendAndSessionFinished(user.getId(), false, false);
        return getCurrentDish(currentSession);
    }
}
