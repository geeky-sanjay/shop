package com.onlineshop.shop.notification.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlineshop.shop.notification.dtos.EmailFormat;
import com.onlineshop.shop.notification.dtos.SmsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendEmail(EmailFormat emailRequest) {
        try {
            String message = new ObjectMapper().writeValueAsString(emailRequest);
            kafkaTemplate.send("sendEmail", message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting email request to JSON", e);
        }
    }

    public void sendSms(SmsRequest smsRequest) {
        try {
            String message = new ObjectMapper().writeValueAsString(smsRequest);
            kafkaTemplate.send("sendSms", message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting SMS request to JSON", e);
        }
    }
}
