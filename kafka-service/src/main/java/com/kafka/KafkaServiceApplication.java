package com.kafka;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class KafkaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaServiceApplication.class, args);
    }
//
//    @KafkaListener(topics = "notificationTopic")
//    public void handleNotification(EvidenceCreateEvent evidenceCreateEvent) {
//        log.info("Received notification for evidence - {}", evidenceCreateEvent.getEvidenceNumber());
//    }
}