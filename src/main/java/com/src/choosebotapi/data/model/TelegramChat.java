package com.src.choosebotapi.data.model;


import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity(name = "telegram_chat")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table
@Getter
@Setter
public class TelegramChat extends AbstractTelegramEntity {

    @Column(nullable = false)
    @Id
    Long id;

    Boolean userChat = true;
    Boolean groupChat = false;
    Boolean channelChat = false;
    Boolean superGroupChat = false;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    TelegramUser user;

}