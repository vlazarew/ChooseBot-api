package com.src.choosebotapi.data.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity(name = "credential")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table
@Getter
@Setter
public class Credential extends DefaultEntity{

    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credential_generator")
    Long id;

    @NotNull @NotEmpty(message = "login must be filled")
    @Column(unique = true)
    String login;

    @NotEmpty @NotNull String password;
}
