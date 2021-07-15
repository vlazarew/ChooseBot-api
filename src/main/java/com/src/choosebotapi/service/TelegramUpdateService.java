package com.src.choosebotapi.service;

import com.src.choosebotapi.data.model.*;
import com.src.choosebotapi.data.repository.*;
import com.src.choosebotapi.telegram.utils.mapper.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.CompletableFuture;

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
    @Async
    public CompletableFuture<TelegramUpdate> save(Update update, Message message, boolean hasContact,
                                                  boolean hasLocation) {

        // Находим персонажа или создаем его
        CompletableFuture<TelegramUser> userFuture = saveFindUser(message);

        // Находим или создаем чат
        return userFuture.thenApply(user -> {
            CompletableFuture<TelegramChat> telegramChat = saveFindChat(message, user);

            // Сохранение контакта
            CompletableFuture<TelegramContact> telegramContact = hasContact ? saveFindContact(message, user) : null;

            // Сохранение локации
            CompletableFuture<TelegramLocation> telegramLocation = hasLocation ? saveFindLocation(update, user) : null;

            // Запись истории сообщений
            TelegramMessage telegramMessage = saveTelegramMessage(message, user, telegramChat.join(),
                    telegramContact == null ? null : telegramContact.join(),
                    telegramLocation == null ? null : telegramLocation.join());

            // Сохраняем все наши обновления
            return saveTelegramUpdate(update, telegramMessage);
        });
    }

    @Async
    CompletableFuture<TelegramUser> saveFindUser(Message message) {
        return CompletableFuture.completedFuture(userRepository.findById(message.getFrom().getId())).thenApply(
                telegramUser -> telegramUser.orElseGet(() -> {
                    TelegramUser transformedUser = telegramUserMapper.toEntity(message.getFrom());
                    transformedUser.setStatus(UserStatus.getInitialStatus());
                    return userRepository.save(transformedUser);
                })
        );
    }

    @Async
    CompletableFuture<TelegramChat> saveFindChat(Message message, TelegramUser user) {
        Chat chat = message.getChat();
        return CompletableFuture.completedFuture(telegramChatRepository.findById(chat.getId())).thenApply(
                telegramChat -> telegramChat.orElseGet(() -> {
                    TelegramChat transformedChat = telegramChatMapper.toEntity(chat);
                    transformedChat.setUser(user);
                    return telegramChatRepository.save(transformedChat);
                }));
    }

    @Async
    CompletableFuture<TelegramContact> saveFindContact(Message message, TelegramUser user) {
        return CompletableFuture.completedFuture(telegramContactRepository.findById(user.getId())).thenApply(
                telegramContact -> telegramContact.orElseGet(() -> {
                    TelegramContact transformedContact = telegramContactMapper.toEntity(message.getContact());
                    transformedContact.setUser(user);

                    // Пользователю сохраняем номер телефона
                    CompletableFuture.runAsync(() -> setUserPhone(user, transformedContact));

                    return telegramContactRepository.save(transformedContact);
                })
        );
    }

    @Async
    void setUserPhone(TelegramUser user, TelegramContact transformedContact) {
        user.setPhoneNumber(transformedContact.getPhoneNumber());
        userRepository.save(user);
    }

    @Async
    CompletableFuture<TelegramLocation> saveFindLocation(Update update, TelegramUser user) {
        Location location = update.getMessage().getLocation();
        float longitude = location.getLongitude().floatValue();
        float latitude = location.getLatitude().floatValue();

        return CompletableFuture.completedFuture(telegramLocationRepository.findByLongitudeAndLatitude(longitude, latitude))
                .thenApply(telegramLocation -> telegramLocation.orElseGet(() -> {
                    TelegramLocation transformedLocation = telegramLocationMapper.toEntity(location);
                    transformedLocation.setUser(user);

                    user.setLocation(transformedLocation);
                    userRepository.save(user);

                    return telegramLocationRepository.save(transformedLocation);
                }));
    }

    TelegramMessage saveTelegramMessage(Message message, TelegramUser user, TelegramChat telegramChat,
                                        TelegramContact telegramContact, TelegramLocation telegramLocation) {
        TelegramMessage telegramMessage = telegramMessageMapper.toEntity(message);
        telegramMessage.setFrom(user);
        telegramMessage.setChat(telegramChat);
        telegramMessage.setContact(telegramContact);
        telegramMessage.setLocation(telegramLocation);
        return messageRepository.save(telegramMessage);
    }

    TelegramUpdate saveTelegramUpdate(Update update, TelegramMessage message) {
        TelegramUpdate telegramUpdate = telegramUpdateMapper.toEntity(update);
        telegramUpdate.setMessage(message);
        return telegramUpdateRepository.save(telegramUpdate);
    }
}
