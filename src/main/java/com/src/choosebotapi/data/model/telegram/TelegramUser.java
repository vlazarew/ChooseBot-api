package com.src.choosebotapi.data.model.telegram;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity(name = "telegram_user")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table
@Getter
@Setter
public class TelegramUser extends AbstractTelegramEntity {

    @Column(nullable = false)
    @Id
    Long id;

    String userName;

    String firstName;
    String lastName;

    String fullName;

    String phoneNumber;
    UserStatus status;

    String languageCode;

    Boolean blocked = false;
    LocalDateTime blockTime = null;
}
