package com.onlineshop.shop.notification.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlineshop.shop.notification.dtos.EmailFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailConsumer {
    private final ObjectMapper objectMapper;

    @Value("${EMAIL_FROM}")
    private String fromEmail;

    @Value("${EMAIL_KEY}")
    private String password;

    private static final Logger logger = LoggerFactory.getLogger(EmailConsumer.class);

    public EmailConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "sendEmail", id = "emailConsumerGroup")
    public void sendEmail(String message) {
        try {
            EmailFormat emailMessage = objectMapper.readValue(message, EmailFormat.class);
            sendActualEmail(emailMessage);
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON message: {}", e.getMessage());
        }
    }

    private void sendActualEmail(EmailFormat emailMessage) {
        logger.info("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session, emailMessage.getTo(), emailMessage.getSubject(), emailMessage.getContent());
    }
}
