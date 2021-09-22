package com.src.choosebotapi.service;

import com.src.choosebotapi.data.model.restaurant.Session;
import com.src.choosebotapi.data.repository.restaurant.SessionRepository;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@EnableAsync
@EnableScheduling
public class SessionService {

    @Autowired
    SessionRepository sessionRepository;

    @Scheduled(cron = "00 */5 * * * *")
//    @Scheduled(fixedDelay = 10000)
    @Async
    @Synchronized
    public void closeSessions() {
        LocalDateTime currentDate = LocalDateTime.now();
        ArrayList<Session> sessions = sessionRepository.findByLastUpdateDateBeforeAndSessionFinished(currentDate.minusHours(2), false);
        for (Session session : sessions) {
            session.setSessionFinished(true);
            sessionRepository.save(session);
        }
    }

}
