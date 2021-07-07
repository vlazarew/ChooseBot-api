package com.src.choosebotapi.telegram.utils.mapper;

import com.src.choosebotapi.data.model.TelegramUpdate;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramUpdateMapper extends AbstractMapper<TelegramUpdate, Update> {

    ModelMapper mapper;

    @Autowired
    TelegramUpdateMapper(ModelMapper mapper) {
        super(TelegramUpdate.class, Update.class);
        this.mapper = mapper;
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(Update.class, TelegramUpdate.class)
                .addMappings(m -> m.map(Update::getUpdateId, TelegramUpdate::setId)).setPostConverter(toEntityConverter());
    }
}
