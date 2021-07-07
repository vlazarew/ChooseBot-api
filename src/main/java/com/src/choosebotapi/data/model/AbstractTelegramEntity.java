package com.src.choosebotapi.data.model;

import lombok.Data;
import lombok.Setter;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
@Setter
public abstract class AbstractTelegramEntity extends DefaultEntity {
}
