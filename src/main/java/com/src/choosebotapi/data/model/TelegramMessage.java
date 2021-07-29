package com.src.choosebotapi.data.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity(name = "telegram_message")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table
@Getter
@Setter
public class TelegramMessage extends AbstractTelegramEntity {
    @Column(nullable = false)
    @Id
    Long id;

    Long messageId;

    String text;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "from_id")
    TelegramUser from;

    LocalDateTime date;

    @OneToOne(cascade = CascadeType.ALL)
    TelegramContact contact;

    @ManyToOne(cascade = CascadeType.ALL)
    TelegramLocation location;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "chat_id")
    TelegramChat chat;
}
