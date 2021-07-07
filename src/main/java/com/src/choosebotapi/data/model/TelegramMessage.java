package com.src.choosebotapi.data.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity(name = "telegram_message")
@Data
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

    String value;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "from_id")
    TelegramUser from;

    @OneToOne
    TelegramContact contact;

    @OneToOne
    TelegramLocation location;

    @OneToOne
    @JoinColumn(name = "chat_id")
    TelegramChat chat;
}
