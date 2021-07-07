package com.src.choosebotapi.data.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity(name = "user")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
@Getter
@Setter
public class User {

    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @NotEmpty @NotNull String userName;

    @NotEmpty @NotNull String name;
    @NotEmpty @NotNull String lastname;

    String phoneNumber;
    Boolean registered = false;
    UserStatus status;


}
