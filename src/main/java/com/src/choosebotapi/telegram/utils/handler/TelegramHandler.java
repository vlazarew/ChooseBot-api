package com.src.choosebotapi.telegram.utils.handler;

import com.src.choosebotapi.data.model.TelegramUpdate;
import com.src.choosebotapi.data.model.TelegramUser;
import com.src.choosebotapi.data.model.UserStatus;
import com.src.choosebotapi.data.repository.TelegramChatRepository;
import com.src.choosebotapi.data.repository.TelegramLocationRepository;
import com.src.choosebotapi.data.repository.TelegramUserRepository;
import com.src.choosebotapi.telegram.TelegramBot;
import com.src.choosebotapi.telegram.TelegramKeyboards;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.CompletableFuture;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
@PropertySource("classpath:commands.properties")
public class TelegramHandler implements TelegramMessageHandler {

    @Autowired
    TelegramBot telegramBot;
    @Autowired
    public TelegramUserRepository userRepository;
    @Autowired
    public TelegramChatRepository telegramChatRepository;
    //        @Autowired
//    public NotificationServiceSettingsRepository notificationServiceSettingsRepository;
    @Autowired
    public TelegramKeyboards telegramKeyboards;
    //    @Autowired
//    public WeatherSettingsRepository weatherSettingsRepository;
//    @Autowired
//    public YandexWeatherService yandexWeatherService;
    @Autowired
    public TelegramLocationRepository telegramLocationRepository;
//    @Autowired
//    public NewsSettingsRepository newsSettingsRepository;
//    @Autowired
//    public NewsItemRepository newsItemRepository;
//    @Autowired
//    public TwitterSettingsRepository twitterSettingsRepository;
//    @Autowired
//    public TwitterHashtagRepository twitterHashtagRepository;
//    @Autowired
//    public TwitterPeopleRepository twitterPeopleRepository;
//    @Autowired
//    public TweetRepository tweetRepository;
//    @Autowired
//    public WeatherCityRepository weatherCityRepository;

    @Value("${telegram.START_COMMAND}")
    public String START_COMMAND;

    @Value("${telegram.HELLO_BUTTON}")
    public String HELLO_BUTTON;

    @Value("${telegram.SHARE_PHONE_NUMBER}")
    public String SHARE_PHONE_NUMBER;

    @Value("${telegram.SHARE_LOCATION}")
    public String SHARE_LOCATION;

    @Value("${telegram.SKIP}")
    public String SKIP;

    @Value("${telegram.WANT_TO_EAT}")
    public String WANT_TO_EAT;

    @Value("${telegram.ENTER_DISH_NAME}")
    public String ENTER_DISH_NAME;

    @Value("${telegram.GET_DISH_RECOMMENDATION}")
    public String GET_DISH_RECOMMENDATION;

    @Value("${telegram.DOWN_1K}")
    public String DOWN_1K;

    @Value("${telegram.FROM_1K_TO_2K}")
    public String FROM_1K_TO_2K;

    @Value("${telegram.UPPER_2K}")
    public String UPPER_2K;

    @Value("${telegram.SNACK_CATEGORY}")
    public String SNACK_CATEGORY;

    @Value("${telegram.SALAD_CATEGORY}")
    public String SALAD_CATEGORY;

    @Value("${telegram.SOUP_CATEGORY}")
    public String SOUP_CATEGORY;

    @Value("${telegram.HOT_DISHES_CATEGORY}")
    public String HOT_DISHES_CATEGORY;

    @Value("${telegram.DESSERT_CATEGORY}")
    public String DESSERT_CATEGORY;

    @Value("${telegram.ALCOHOLIC_DRINKS_CATEGORY}")
    public String ALCOHOLIC_DRINKS_CATEGORY;

    @Value("${telegram.SOFT_DRINKS_CATEGORY}")
    public String SOFT_DRINKS_CATEGORY;

    @Value("${telegram.SUSHI_ROLLS_CATEGORY}")
    public String SUSHI_ROLLS_CATEGORY;

    @Value("${telegram.BREAKFAST_CATEGORY}")
    public String BREAKFAST_CATEGORY;

    @Value("${telegram.EUROPEAN_KITCHEN_DIRECTION}")
    public String EUROPEAN_KITCHEN_DIRECTION;

    @Value("${telegram.RUSSIAN_KITCHEN_DIRECTION}")
    public String RUSSIAN_KITCHEN_DIRECTION;

    @Value("${telegram.ITALIAN_KITCHEN_DIRECTION}")
    public String ITALIAN_KITCHEN_DIRECTION;

    @Value("${telegram.CAUCASIAN_KITCHEN_DIRECTION}")
    public String CAUCASIAN_KITCHEN_DIRECTION;

    @Value("${telegram.ASIAN_KITCHEN_DIRECTION}")
    public String ASIAN_KITCHEN_DIRECTION;

    @Override
    public void handle(TelegramUpdate telegramUpdate, boolean hasText, boolean hasContact, boolean hasLocation) {
    }

    @Override
    public void sendMessageToUserByCustomMainKeyboard(Long chatId, TelegramUser telegramUser, String text, UserStatus status) {
        telegramKeyboards.getCustomReplyMainKeyboardMarkup(telegramUser)
                .thenCompose(replyKeyboardMarkup ->
                        CompletableFuture.runAsync(() -> sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status)));
        ;
    }

    public void sendMessageVerifyPhoneNumber(Long chatId, String text, UserStatus status) {
        telegramKeyboards.getSharePhoneNumberKeyboardMarkup().thenCompose(
                replyKeyboardMarkup ->
                        CompletableFuture.runAsync(() -> sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status))
        );
    }

    public void sendMessageShareLocation(Long chatId, String text, UserStatus status) {
        telegramKeyboards.getShareLocationKeyboardMarkup().thenCompose(
                replyKeyboardMarkup ->
                        CompletableFuture.runAsync(() -> sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status))
        );
    }

    public void sendMessageWantToEat(Long chatId, String text, UserStatus status) {
        telegramKeyboards.getWantToEatKeyboardMarkup().thenCompose(
                replyKeyboardMarkup ->
                        CompletableFuture.runAsync(() -> sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status))
        );
    }

    public void sendMessageEnterDishOrGetRecommendations(Long chatId, String text, UserStatus status) {
        telegramKeyboards.getEnterDishOrGetRecommendationsKeyboardMarkup().thenCompose(
                replyKeyboardMarkup ->
                        CompletableFuture.runAsync(() -> sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status))
        );
    }

    public void sendSelectAverageCheck(Long chatId, String text, UserStatus status) {
        telegramKeyboards.getSelectAverageCheckKeyboardMarkup().thenCompose(
                replyKeyboardMarkup ->
                        CompletableFuture.runAsync(() -> sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status))
        );
    }

    public void sendSelectDishCategory(Long chatId, String text, UserStatus status) {
        telegramKeyboards.getSelectDishCategoryKeyboardMarkup().thenCompose(
                replyKeyboardMarkup ->
                        CompletableFuture.runAsync(() -> sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status))
        );
    }

    public void sendSelectDishKitchenDirection(Long chatId, String text, UserStatus status) {
        telegramKeyboards.getSelectDishKitchenDirectionKeyboardMarkup().thenCompose(
                replyKeyboardMarkup ->
                        CompletableFuture.runAsync(() -> sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status))
        );
    }

    @Override
    public void sendTextMessageReplyKeyboardMarkup(Long chatId, String text, ReplyKeyboardMarkup replyKeyboardMarkup,
                                                   UserStatus status) {
        CompletableFuture.supplyAsync(() -> makeSendMessageWithKeyboard(chatId, text, replyKeyboardMarkup))
                .thenCompose(sendMessage ->
                        CompletableFuture.runAsync(() -> executeSendingMessage(chatId, status, sendMessage)));
    }

    @Override
    public void sendTextMessageWithoutKeyboard(Long chatId, String text, UserStatus status) {
        CompletableFuture.supplyAsync(() -> makeSendMessageWithoutKeyboard(chatId, text))
                .thenCompose(sendMessage ->
                        CompletableFuture.runAsync(() -> executeSendingMessage(chatId, status, sendMessage)));
    }

    private void executeSendingMessage(Long chatId, UserStatus status, SendMessage sendMessage) {
        CompletableFuture.runAsync(() -> {
            try {
                telegramBot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });

        if (status != null) {
            CompletableFuture.supplyAsync(() -> telegramChatRepository.findById(chatId)).thenCompose(
                    telegramChat -> {
                        if (telegramChat.isPresent()) {
                            TelegramUser user = telegramChat.get().getUser();
                            user.setStatus(status);
                            userRepository.save(user);
                        } else {
                            log.error("Не найден чат с id: " + chatId);
                        }
                        return null;
                    }
            );
        }
    }

    private SendMessage makeSendMessageWithKeyboard(Long chatId, String text, ReplyKeyboardMarkup replyKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    private SendMessage makeSendMessageWithoutKeyboard(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        ReplyKeyboardRemove replyKeyboardMarkup = new ReplyKeyboardRemove();
        replyKeyboardMarkup.setRemoveKeyboard(true);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }
}
