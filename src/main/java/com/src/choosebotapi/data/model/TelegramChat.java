package com.src.choosebotapi.data.model;


import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity(name = "telegram_chat")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table
@Getter
@Setter
public class TelegramChat extends AbstractTelegramEntity {

    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    Boolean userChat = true;
    Boolean groupChat = false;
    Boolean channelChat = false;
    Boolean superGroupChat = false;

    @OneToOne
    @JoinColumn(name = "user_id")
    TelegramUser user;

}
