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
public class TelegramMessage extends AbstractTelegramEntity{
    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String text;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "from_id")
    TelegramUser from;

    LocalDateTime date;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    TelegramContact contact;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    TelegramLocation location;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    TelegramChat chat;
}
