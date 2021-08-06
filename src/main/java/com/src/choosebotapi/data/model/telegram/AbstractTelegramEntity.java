package com.src.choosebotapi.data.model.telegram;

import com.src.choosebotapi.data.model.DefaultEntity;
import lombok.Setter;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Setter
public abstract class AbstractTelegramEntity extends DefaultEntity {
}
