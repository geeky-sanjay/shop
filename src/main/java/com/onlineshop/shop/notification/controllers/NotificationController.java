package com.onlineshop.shop.notification.controllers;

import com.onlineshop.shop.notification.dtos.EmailFormat;
import com.onlineshop.shop.notification.dtos.SmsRequest;
import com.onlineshop.shop.notification.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send-email")
    public void sendEmail(@RequestBody EmailFormat emailRequest) {
        notificationService.sendEmail(emailRequest);
    }

    @PostMapping("/send-sms")
    public void sendSms(@RequestBody SmsRequest smsRequest) {
        notificationService.sendSms(smsRequest);
    }
}
