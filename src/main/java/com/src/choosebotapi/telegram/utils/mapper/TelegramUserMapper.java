package com.src.choosebotapi.telegram.utils.mapper;

import com.src.choosebotapi.data.model.TelegramMessage;
import com.src.choosebotapi.data.model.TelegramUser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class TelegramUserMapper extends AbstractMapper<TelegramUser, User> {

    ModelMapper mapper;

    @Autowired
    TelegramUserMapper(ModelMapper mapper) {
        super(TelegramUser.class, User.class);
        this.mapper = mapper;
    }

    @Autowired
    public void setupMapper() {
        mapper.createTypeMap(User.class, TelegramUser.class)
                .addMappings(m -> {
                    m.map(User::getId, TelegramUser::setTelegramId);
                    m.map(User::getFirstName, TelegramUser::setFirstName);
                    m.map(User::getLastName, TelegramUser::setLastName);
                    m.map(User::getLanguageCode, TelegramUser::setLanguageCode);
                }).setPostConverter(toEntityConverter());
    }
}
