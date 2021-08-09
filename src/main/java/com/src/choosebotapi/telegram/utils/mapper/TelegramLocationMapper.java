package com.src.choosebotapi.telegram.utils.mapper;

import com.src.choosebotapi.data.model.telegram.TelegramLocation;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Location;

import javax.annotation.PostConstruct;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramLocationMapper extends AbstractMapper<TelegramLocation, Location> {

    ModelMapper mapper;

    @Autowired
    TelegramLocationMapper(ModelMapper mapper) {
        super(TelegramLocation.class, Location.class);
        this.mapper = mapper;
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(Location.class, TelegramLocation.class)
                .addMappings(m -> {
                    m.map(Location::getLatitude, TelegramLocation::setLatitude);
                    m.map(Location::getLongitude, TelegramLocation::setLongitude);
                }).setPostConverter(toEntityConverter());

    }
}
