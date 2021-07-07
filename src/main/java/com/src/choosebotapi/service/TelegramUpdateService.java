package com.src.choosebotapi.service;

import com.src.choosebotapi.data.model.*;
import com.src.choosebotapi.data.repository.*;
import com.src.choosebotapi.telegram.utils.mapper.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TelegramUpdateService {

    TelegramChatRepository telegramChatRepository;
    TelegramMessageRepository messageRepository;
    TelegramUpdateRepository telegramUpdateRepository;
    TelegramUserRepository userRepository;
    TelegramContactRepository telegramContactRepository;
    TelegramLocationRepository telegramLocationRepository;

    TelegramUserMapper telegramUserMapper;
    TelegramChatMapper telegramChatMapper;
    TelegramContactMapper telegramContactMapper;
    TelegramLocationMapper telegramLocationMapper;
    TelegramMessageMapper telegramMessageMapper;
    TelegramUpdateMapper telegramUpdateMapper;

    // Турбо метод, записывающий все изменения, которые пришли по апдейту
    public TelegramUpdate save(Update update) {

        Message message = update.getMessage();
        boolean hasContact = message.hasContact();
        boolean hasLocation = message.hasLocation();

        // Находим персонажа или создаем его
        TelegramUser user = saveFindUser(message);

        // Находим или создаем чат
        TelegramChat telegramChat = saveFindChat(message, user);

        // Сохранение контакта
        TelegramContact telegramContact = hasContact ? saveFindContact(message, user) : null;

        // Сохранение локации
        TelegramLocation telegramLocation = hasLocation ? saveFindLocation(update, user) : null;

        // Запись истории сообщений
        TelegramMessage telegramMessage = saveTelegramMessage(message, user, telegramChat, telegramContact
                , telegramLocation);

        // Сохраняем все наши обновления
        return saveTelegramUpdate(update, telegramMessage);
    }

    private TelegramUser saveFindUser(Message message) {
        return userRepository.findById(message.getFrom().getId())
                .orElseGet(() -> {
                    TelegramUser transformedUser = telegramUserMapper.toEntity(message.getFrom());
                    transformedUser.setStatus(UserStatus.getInitialStatus());
                    return userRepository.save(transformedUser);
                });
    }

    private TelegramChat saveFindChat(Message message, TelegramUser user) {
        Chat chat = message.getChat();
        return telegramChatRepository.findById(chat.getId())
                .orElseGet(() -> {
                    TelegramChat transformedChat = telegramChatMapper.toEntity(chat);
                    transformedChat.setUser(user);

                    return telegramChatRepository.save(transformedChat);
                });
    }

    private TelegramContact saveFindContact(Message message, TelegramUser user) {
        return telegramContactRepository.findById(user.getId())
                .orElseGet(() -> {
                    TelegramContact transformedContact = telegramContactMapper.toEntity(message.getContact());
                    transformedContact.setUser(user);

                    // Пользователю сохраняем номер телефона
                    setUserPhone(user, transformedContact);

                    return telegramContactRepository.save(transformedContact);
                });
    }

    private void setUserPhone(TelegramUser user, TelegramContact transformedContact) {
        user.setPhoneNumber(transformedContact.getPhoneNumber());
        userRepository.save(user);
    }

    private TelegramLocation saveFindLocation(Update update, TelegramUser user) {
        Location location = update.getMessage().getLocation();
        float longitude = location.getLongitude().floatValue();
        float latitude = location.getLatitude().floatValue();

        return telegramLocationRepository.findByLongitudeAndLatitude(longitude, latitude)
                .orElseGet(() -> {
                    TelegramLocation transformedLocation = telegramLocationMapper.toEntity(location);
                    transformedLocation.setUser(user);

                    user.setLocation(transformedLocation);
                    userRepository.save(user);

                    return telegramLocationRepository.save(transformedLocation);
                });
    }

    private TelegramMessage saveTelegramMessage(Message message, TelegramUser user, TelegramChat telegramChat,
                                                TelegramContact telegramContact, TelegramLocation telegramLocation) {
        TelegramMessage telegramMessage = telegramMessageMapper.toEntity(message);
        telegramMessage.setFrom(user);
        telegramMessage.setChat(telegramChat);
        telegramMessage.setContact(telegramContact);
        telegramMessage.setLocation(telegramLocation);
        return messageRepository.save(telegramMessage);
    }

    private TelegramUpdate saveTelegramUpdate(Update update, TelegramMessage message) {
        TelegramUpdate telegramUpdate = telegramUpdateMapper.toEntity(update);
        telegramUpdate.setMessage(message);
        return telegramUpdateRepository.save(telegramUpdate);
    }
}
