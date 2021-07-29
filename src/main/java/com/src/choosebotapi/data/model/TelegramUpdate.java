package com.src.choosebotapi.data.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity(name = "telegram_update")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table
@Getter
@Setter
public class TelegramUpdate extends AbstractTelegramEntity {

    @Column(nullable = false)
    @Id
    Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "message_id")
    TelegramMessage message;
}
