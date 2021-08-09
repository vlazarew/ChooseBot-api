package com.src.choosebotapi.data.repository.restaurant;

import com.src.choosebotapi.data.model.restaurant.Session;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
public interface SessionRepository extends CrudRepository<Session, Long> {

    Session findByUser_IdAndNotificationSendAndSessionFinished(Long userId, boolean notificationSend, boolean sessionFinished);

}