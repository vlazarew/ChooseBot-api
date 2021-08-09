package com.src.choosebotapi.data.repository.telegram;

import com.src.choosebotapi.data.model.telegram.TelegramLocation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import java.util.Optional;

@RepositoryRestController
public interface TelegramLocationRepository extends CrudRepository<TelegramLocation, Long> {
    Optional<TelegramLocation> findByLongitudeAndLatitude(Float longitude, Float latitude);

}
