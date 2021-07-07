package com.src.choosebotapi.telegram.utils.handler;

import com.src.choosebotapi.data.model.TelegramUpdate;
import com.src.choosebotapi.data.repository.TelegramChatRepository;
import com.src.choosebotapi.data.repository.TelegramLocationRepository;
import com.src.choosebotapi.data.repository.TelegramUserRepository;
import com.src.choosebotapi.telegram.TelegramBot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
@PropertySource("classpath:ui.properties")
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
//    @Autowired
//    public TelegramKeyboards telegramKeyboards;
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

    @Override
    public void handle(TelegramUpdate telegramUpdate, boolean hasText, boolean hasContact, boolean hasLocation) {

    }
}
