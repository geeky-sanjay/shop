package com.onlineshop.shop.authentication.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlineshop.shop.authentication.dtos.EmailFormat;
import com.onlineshop.shop.authentication.exceptions.InvalidCredentialsException;
import com.onlineshop.shop.authentication.models.Token;
import com.onlineshop.shop.authentication.models.User;
import com.onlineshop.shop.authentication.repositories.TokenRepository;
import com.onlineshop.shop.authentication.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
  //  private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserService(UserRepository userRepository, TokenRepository tokenRepository, BCryptPasswordEncoder bCryptPasswordEncoder, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
       // this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public User signUp(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));
        return userRepository.save(user);
//        try {
//            kafkaTemplate.send("sendEmail", objectMapper.writeValueAsString(getMessage(user))).get();
//            return userRepository.save(user);
//        } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
//            throw new RuntimeException("Failed to send Kafka message", e);
//        }
    }

    public EmailFormat getMessage(User user) {
        EmailFormat message = new EmailFormat();
        message.setTo(user.getEmail());
        message.setFrom("geekysanjay@gmail.com");
        message.setContent("Successfully signed up");
        message.setSubject("Sign up success");
        return message;
    }

    public Token login(String email, String password) throws InvalidCredentialsException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new InvalidCredentialsException();
        }

        if (!bCryptPasswordEncoder.matches(password, user.get().getHashedPassword())) {
            throw new InvalidCredentialsException();
        }

        Token token = new Token();
        token.setUser(user.get());
        token.setValue(UUID.randomUUID().toString());
        token.setExpirydate(get30DaysLaterDate());
        return tokenRepository.save(token);
    }

    private Date get30DaysLaterDate() {
        LocalDate currentDate = LocalDate.now().plusDays(30);
        return Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void logout(String token) {
        Optional<Token> optionalToken = tokenRepository.findByValueAndIsDeletedEquals(token, false);
        if (optionalToken.isEmpty()) {
            return;
        }
        optionalToken.get().setDeleted(true);
        tokenRepository.save(optionalToken.get());
    }
}
