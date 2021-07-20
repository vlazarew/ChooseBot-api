package com.src.choosebotapi.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.src.choosebotapi.service.google.GoogleConnection;
import com.src.choosebotapi.service.google.GoogleConnectionService;
import com.src.choosebotapi.service.google.GoogleSpreadSheetService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@EnableAsync
@EnableScheduling
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationService {

    @Autowired
    GoogleSpreadSheetService googleSpreadSheetService;

    @Autowired
    GoogleConnection googleConnection;

    final long updatePeriod = 5000;


    @Scheduled(fixedRate = updatePeriod)
    @Async
    public void checkNotification() throws IOException {
        googleSpreadSheetService.readTable(googleConnection);
    }
}
