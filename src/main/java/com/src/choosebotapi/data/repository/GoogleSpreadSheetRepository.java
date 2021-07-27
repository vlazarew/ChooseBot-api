package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.GoogleSpreadSheet;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Optional;

@RepositoryRestController
public interface GoogleSpreadSheetRepository extends CrudRepository<GoogleSpreadSheet, Long> {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="2000")})
    Optional<GoogleSpreadSheet> findByBloggerNicknameAndDateTimeOfRecordAndRestaurantNameAndDishName(String bloggerNickname, Long dateTimeOfRecord, String restaurantName, String dishName);

}
