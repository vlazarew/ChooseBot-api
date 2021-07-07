package com.src.choosebotapi.telegram.utils.mapper;

import com.src.choosebotapi.data.model.TelegramChat;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramChatMapper extends AbstractMapper<TelegramChat, Chat> {

    @Autowired
    public TelegramChatMapper(ModelMapper mapper) {
        super(TelegramChat.class, Chat.class);
    }

}
