package com.src.choosebotapi.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
@EnableScheduling
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationService {

    @Scheduled(cron = "0 1/1 * * * *")
    @Async
    public void checkNotification() {
    }
}
