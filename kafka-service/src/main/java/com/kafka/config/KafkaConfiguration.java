//package com.notification.config;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.TopicBuilder;
//
//@EnableKafka
//@Configuration
//public class KafkaConfiguration {
//
//    @Bean
//    public NewTopic topicOrder() {
//        return TopicBuilder.name("notificationTopic").partitions(2).replicas(1).build();
//    }
//}
