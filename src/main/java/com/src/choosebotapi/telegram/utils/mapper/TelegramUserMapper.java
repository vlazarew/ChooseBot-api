package com.src.choosebotapi.telegram.utils.mapper;

import com.src.choosebotapi.data.model.TelegramUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class TelegramUserMapper extends AbstractMapper<TelegramUser, User> {

    @Autowired
    TelegramUserMapper() {
        super(TelegramUser.class, User.class);
    }
}
