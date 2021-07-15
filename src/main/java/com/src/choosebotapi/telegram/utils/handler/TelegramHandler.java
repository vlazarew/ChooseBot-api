package com.src.choosebotapi.telegram.utils.handler;

import com.src.choosebotapi.data.model.TelegramChat;
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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
@PropertySource("classpath:commands.properties")
@EnableAsync
public class TelegramHandler implements TelegramMessageHandler {

    @Autowired
    TelegramBot telegramBot;
    @Autowired
    public TelegramUserRepository userRepository;
    @Autowired
    public TelegramChatRepository telegramChatRepository;
    //    @Autowired
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

    @Value("${telegram.CONFIRM_FULLNAME_FOR_ORDER}")
    public String CONFIRM_FULLNAME_FOR_ORDER;

    @Override
    public void handle(TelegramUpdate telegramUpdate, boolean hasText, boolean hasContact, boolean hasLocation) {

    }

    @Override
    public void sendMessageToUserByCustomMainKeyboard(Long chatId, TelegramUser telegramUser, String text, UserStatus status) {
        ReplyKeyboardMarkup replyKeyboardMarkup = telegramKeyboards.getCustomReplyMainKeyboardMarkup(telegramUser);
        sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status);
    }

    public void sendMessageVerifyFullName(Long chatId, TelegramUser telegramUser, String text, UserStatus status){
        ReplyKeyboardMarkup replyKeyboardMarkup = telegramKeyboards.getConfirmFullNameToOrderKeyboardMarkup();
        sendTextMessageReplyKeyboardMarkup(chatId, text, replyKeyboardMarkup, status);
    }

    @Override
    public void sendTextMessageReplyKeyboardMarkup(Long chatId, String text, ReplyKeyboardMarkup replyKeyboardMarkup,
                                                   UserStatus status) {
        SendMessage sendMessage = makeSendMessageWithKeyboard(chatId, text, replyKeyboardMarkup);
        executeSendingMessage(chatId, status, sendMessage);
    }

    @Override
    public void sendTextMessageWithoutKeyboard(Long chatId, String text, UserStatus status) {
        SendMessage sendMessage = makeSendMessageWithoutKeyboard(chatId, text);
        executeSendingMessage(chatId, status, sendMessage);
    }

    private void executeSendingMessage(Long chatId, UserStatus status, SendMessage sendMessage) {
        try {
            telegramBot.execute(sendMessage);

            if (status != null) {
                TelegramUser user;
                Optional<TelegramChat> chat = telegramChatRepository.findById(chatId);

                if (chat.isPresent()) {
                    user = chat.get().getUser();

                    user.setStatus(status);
                    userRepository.save(user);
                } else {
                    log.error("Не найден чат с id: " + chatId);
                }
            }

        } catch (TelegramApiException e) {
            log.error(e);
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

        return sendMessage;
    }
}
