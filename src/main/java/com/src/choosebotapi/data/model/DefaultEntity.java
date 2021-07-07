package com.src.choosebotapi.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Data
@Setter
public abstract class DefaultEntity implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-HH hh:mm:ss")
    LocalDateTime creationDate;

    @PrePersist
    public void toCreate() {
        setCreationDate(LocalDateTime.now());
    }
}
