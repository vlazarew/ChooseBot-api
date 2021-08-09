package com.src.choosebotapi.telegram.utils.mapper;

import com.src.choosebotapi.data.model.telegram.AbstractTelegramEntity;

public interface Mapper<E extends AbstractTelegramEntity, D> {

    E toEntity(D dto);

    D toDTO(E entity);

}
