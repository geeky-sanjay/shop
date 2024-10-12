package com.onlineshop.shop.authentication.services;

import com.onlineshop.shop.authentication.exceptions.InvalidCredentialsException;
import com.onlineshop.shop.authentication.models.BaseModel;
import com.onlineshop.shop.authentication.models.User;
import com.onlineshop.shop.authentication.repositories.UserRepository;
import com.onlineshop.shop.authentication.security.JwtTokenProvider;
import com.onlineshop.shop.notification.dtos.EmailFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseModel implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider,PasswordEncoder  passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${JWT_SECRET}")
    private String jwtSecret; // Injected JWT secret

    /**
     * Loads the user by email.
     *
     * @param email The user's email.
     * @return UserDetails object.
     * @throws UsernameNotFoundException If user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities()
        );
    }

    /**
     * Authenticates the user and generates a JWT token.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @return A JWT token.
     * @throws InvalidCredentialsException If authentication fails.
     */
    public String login(String email, String password) throws InvalidCredentialsException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return jwtTokenProvider.generateToken(user);
    }

    public User signUp(String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
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

    public void logout(String token) {
//        Optional<Token> optionalToken = tokenRepository.findByValueAndIsDeletedEquals(token, false);
//        if (optionalToken.isEmpty()) {
//            return;
//        }
//        optionalToken.get().setDeleted(true);
//        tokenRepository.save(optionalToken.get());
    }
}
