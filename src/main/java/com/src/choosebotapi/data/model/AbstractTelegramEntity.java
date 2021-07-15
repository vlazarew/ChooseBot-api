package com.src.choosebotapi.data.model;

import lombok.Setter;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Setter
public abstract class AbstractTelegramEntity extends DefaultEntity {
}
