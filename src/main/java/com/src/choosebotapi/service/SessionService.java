package com.src.choosebotapi.service;

import com.src.choosebotapi.data.model.restaurant.Session;
import com.src.choosebotapi.data.model.telegram.UserStatus;
import com.src.choosebotapi.data.repository.restaurant.SessionRepository;
import com.src.choosebotapi.telegram.utils.handler.TelegramHandler;
import lombok.AccessLevel;
import lombok.Synchronized;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
@PropertySource("classpath:ui.properties")
public class SessionService {

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    TelegramHandler telegramHandler;

    @Value("${telegram.sessionClosedAutomatically}")
    String sessionClosedAutomatically;

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
            telegramHandler.sendMessageWantToEat(session.getUser().getId(), sessionClosedAutomatically, UserStatus.WantToEat);
        }
    }

}
