package com.src.choosebotapi.telegram.utils.handler;

import com.src.choosebotapi.data.model.restaurant.Dish;
import com.src.choosebotapi.data.model.restaurant.Session;
import com.src.choosebotapi.data.model.telegram.TelegramUpdate;
import com.src.choosebotapi.data.model.telegram.TelegramUser;
import com.src.choosebotapi.data.model.telegram.UserStatus;
import com.src.choosebotapi.data.repository.restaurant.DishCategoryRepository;
import com.src.choosebotapi.data.repository.restaurant.DishKitchenDirectionRepository;
import com.src.choosebotapi.data.repository.restaurant.DishRepository;
import com.src.choosebotapi.data.repository.restaurant.SessionRepository;
import com.src.choosebotapi.data.repository.telegram.TelegramChatRepository;
import com.src.choosebotapi.data.repository.telegram.TelegramLocationRepository;
import com.src.choosebotapi.data.repository.telegram.TelegramUserRepository;
import com.src.choosebotapi.service.yandex.YandexGeoDecoderService;
import com.src.choosebotapi.telegram.TelegramBot;
import com.src.choosebotapi.telegram.TelegramKeyboards;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.src.choosebotapi.data.model.telegram.UserStatus.WantToEat;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
@PropertySource("classpath:commands.properties")
@PropertySource("classpath:ui.properties")
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
    @Autowired
    public DishRepository dishRepository;
    @Autowired
    public SessionRepository sessionRepository;
    @Autowired
    public TelegramLocationRepository telegramLocationRepository;
    @Autowired
    public DishCategoryRepository dishCategoryRepository;
    @Autowired
    public DishKitchenDirectionRepository dishKitchenDirectionRepository;
    @Autowired
    public YandexGeoDecoderService yandexGeoDecoderService;


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

    @Value("${telegram.STATS_COMMAND}")
    public String STATS_COMMAND;

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

    @Value("${telegram.PREVIOUS_DISH}")
    public String PREVIOUS_DISH;

    @Value("${telegram.NEXT_DISH}")
    public String NEXT_DISH;

    @Value("${telegram.SELECT_DISH}")
    public String SELECT_DISH;

    @Value("${telegram.EXIT_DISH}")
    public String EXIT_DISH;

    @Value("${telegram.BOOK_PLACE_IN_RESTAURANT}")
    public String BOOK_PLACE_IN_RESTAURANT;

    @Value("${telegram.MAKE_ROUTE_TO_RESTAURANT}")
    public String MAKE_ROUTE_TO_RESTAURANT;

    @Value("${telegram.hello}")
    String helloMessage;

    @Value("${telegram.wantToEat}")
    String wantToEat;

    @Value("${telegram.sendMessageFailed}")
    String sendMessageFailed;

    @Override
    public void handle(TelegramUpdate telegramUpdate, boolean hasText, boolean hasContact, boolean hasLocation) {
    }

    // @Transactional(rollbackFor = Exception.class)
    public void sendHelloMessage(Long chatId) {
        sendTextMessageWithoutKeyboard(chatId, helloMessage, null);

        ArrayList<Session> sessions = sessionRepository.findByUser_Id(chatId);
        sessions.forEach(session -> {
            session.setSessionFinished(true);
            sessionRepository.save(session);
        });

        sendMessageWantToEat(chatId, wantToEat, WantToEat);
    }

    @Override
    // @Transactional(rollbackFor = Exception.class)
    public void sendMessageToUserByCustomMainKeyboard(Long chatId, TelegramUser telegramUser, String text, UserStatus status) {
        telegramKeyboards.getCustomReplyMainKeyboardMarkup(telegramUser)
                .thenCompose(replyKeyboardMarkup ->
                        CompletableFuture.runAsync(() -> sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status)));
    }

    // @Transactional(rollbackFor = Exception.class)
    public void sendMessageVerifyPhoneNumber(Long chatId, String text, UserStatus status) {
        telegramKeyboards.getSharePhoneNumberKeyboardMarkup().thenCompose(
                replyKeyboardMarkup ->
                        CompletableFuture.runAsync(() -> sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status))
        );
    }

    // @Transactional(rollbackFor = Exception.class)
    public void sendMessageShareLocation(Long chatId, String text, UserStatus status) {
        telegramKeyboards.getShareLocationKeyboardMarkup().thenCompose(
                replyKeyboardMarkup ->
                        CompletableFuture.runAsync(() -> sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status))
        );
    }

    // @Transactional(rollbackFor = Exception.class)
    public void sendMessageWantToEat(Long chatId, String text, UserStatus status) {
        telegramKeyboards.getWantToEatKeyboardMarkup().thenCompose(
                replyKeyboardMarkup ->
                        CompletableFuture.runAsync(() -> sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status))
        );
    }

    // @Transactional(rollbackFor = Exception.class)
    public void sendMessageEnterDishOrGetRecommendations(Long chatId, String text, UserStatus status) {
        telegramKeyboards.getEnterDishOrGetRecommendationsKeyboardMarkup().thenCompose(
                replyKeyboardMarkup ->
                        CompletableFuture.runAsync(() -> sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status))
        );
    }

    // @Transactional(rollbackFor = Exception.class)
    public void sendSelectAverageCheck(Long chatId, String text, UserStatus status) {
        telegramKeyboards.getSelectAverageCheckKeyboardMarkup().thenCompose(
                replyKeyboardMarkup ->
                        CompletableFuture.runAsync(() -> sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status))
        );
    }

    // @Transactional(rollbackFor = Exception.class)
    public void sendSelectDishCategory(Long chatId, String text, UserStatus status) {
        telegramKeyboards.getSelectDishCategoryKeyboardMarkup().thenCompose(
                replyKeyboardMarkup ->
                        CompletableFuture.runAsync(() -> sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status))
        );
    }

    // @Transactional(rollbackFor = Exception.class)
    public void sendSelectDishKitchenDirection(Long chatId, String text, UserStatus status) {
        telegramKeyboards.getSelectDishKitchenDirectionKeyboardMarkup().thenCompose(
                replyKeyboardMarkup ->
                        CompletableFuture.runAsync(() -> sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status))
        );
    }

    // @Transactional(rollbackFor = Exception.class)
    public void sendSelectDishFromTop(Long chatId, String text, Dish dishToPresent, UserStatus status) {
        telegramKeyboards.getSelectDishFromTopKeyboardMarkup(chatId).thenCompose(
                replyKeyboardMarkup -> {
                    byte[] image = dishToPresent.getImage();
                    if (image == null || image.length == 0) {
                        CompletableFuture.runAsync(() -> sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status));
                    } else {
                        CompletableFuture.supplyAsync(() -> makeSendPhotoWithKeyboard(chatId, text, replyKeyboardMarkup))
                                .thenCompose(sendPhoto -> CompletableFuture.runAsync(() -> executeSendingPhoto(chatId, image, sendPhoto, status)));
                    }
                    return null;
                }
        );
    }

    // @Transactional(rollbackFor = Exception.class)
    public void sendSelectBookOrRoute(Long chatId, String text, UserStatus status) {
        telegramKeyboards.getSelectBookOrRouteKeyboardMarkup().thenCompose(
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
                log.error("???????????? ?????? ???????????????? ?????????????????? " + sendMessage.getText() + " ???????????????????????? " + chatId + "\n" +
                        "?????? ????????????: " + e.getMessage());
                if (e.getMessage().contains("[403] Forbidden: bot was blocked by the user")) {
                    TelegramUser user = userRepository.findById(chatId).get();
                    user.setBlocked(true);
                    user.setBlockTime(LocalDateTime.now());
                    userRepository.save(user);
                } else {
                    sendTextMessageWithoutKeyboard(chatId, sendMessageFailed, null);
                }
            }
        });

        updateStatus(chatId, status);
    }

    private void executeSendingPhoto(Long chatId, byte[] image, SendPhoto sendPhoto, UserStatus status) {
        CompletableFuture.runAsync(() -> {
            try {
                File imageFile = makeFile(image);
                sendPhoto.setPhoto(new InputFile(imageFile));
                telegramBot.execute(sendPhoto);
                imageFile.delete();
            } catch (TelegramApiException e) {
                log.error("???????????? ?????? ???????????????? ???????????????????? " + sendPhoto.getCaption() + " ???????????????????????? " + chatId + "\n" +
                        "?????? ????????????: " + e.getMessage());
            }
        });

        updateStatus(chatId, status);
    }

    private void updateStatus(Long chatId, UserStatus status) {
        if (status != null) {
            CompletableFuture.supplyAsync(() -> telegramChatRepository.findById(chatId)).thenCompose(
                    telegramChat -> {
                        if (telegramChat.isPresent()) {
                            TelegramUser user = telegramChat.get().getUser();
                            user.setStatus(status);
                            userRepository.save(user);
                        } else {
                            log.error("???? ???????????? ?????? ?? id: " + chatId);
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

    private SendPhoto makeSendPhotoWithKeyboard(Long chatId, String caption, ReplyKeyboardMarkup replyKeyboardMarkup) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setCaption(caption);

        sendPhoto.setReplyMarkup(replyKeyboardMarkup);
        return sendPhoto;
    }

    private File makeFile(byte[] image) {
        UUID uuid = UUID.randomUUID();
        File imageFile = new File("photos/" + uuid);
        try {
            FileUtils.writeByteArrayToFile(imageFile, image);
        } catch (IOException e) {
            log.error("???????????? ?????? ???????????????? ?????????????????????? ??????????. ?????? ????????????: " + e.getMessage());
        }
        return imageFile;
    }

    public String makeStringBold(String text) {
        return "*" + text + "*";
    }


}
