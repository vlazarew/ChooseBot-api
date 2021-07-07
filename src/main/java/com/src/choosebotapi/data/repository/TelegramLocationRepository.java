package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.TelegramLocation;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import java.util.Optional;

@RepositoryRestController
public interface TelegramLocationRepository extends CrudRepository<TelegramLocation, Long> {
    Optional<TelegramLocation> findByLongitudeAndLatitude(Float longitude, Float latitude);

}
