package com.src.choosebotapi.data.model;

import jdk.jfr.Description;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    Long TelegramId;

    @NotEmpty @NotNull String userName;

    String firstName;
    String lastName;

    @Column(columnDefinition = "name of user for order in restaurant")
    String fullName;

    String phoneNumber;
    Boolean registered = false;
    UserStatus status;

    String languageCode;
    Boolean isBot;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    TelegramLocation location;
}
