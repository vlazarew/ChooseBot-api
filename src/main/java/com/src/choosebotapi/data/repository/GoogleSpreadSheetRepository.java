package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.GoogleSpreadSheet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import java.util.Optional;

@RepositoryRestController
public interface GoogleSpreadSheetRepository extends CrudRepository<GoogleSpreadSheet, Long> {

    Optional<GoogleSpreadSheet> findByBloggerNicknameAndDateTimeOfRecordAndRestaurantNameAndDishName(String bloggerNickname, Long dateTimeOfRecord, String restaurantName, String dishName);

}
