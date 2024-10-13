package com.onlineshop.shop.authentication.services;

import com.onlineshop.shop.authentication.exceptions.InvalidCredentialsException;
import com.onlineshop.shop.authentication.models.BaseModel;
import com.onlineshop.shop.authentication.models.User;
import com.onlineshop.shop.authentication.repositories.UserRepository;
import com.onlineshop.shop.authentication.security.JwtTokenProvider;
import com.onlineshop.shop.notification.dtos.EmailFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService extends BaseModel implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${JWT_SECRET}")
    private String jwtSecret; // Injected JWT secret

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
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
        // Retrieve user by email
        User user = findByEmail(email);

        // Log for debugging
        System.out.println("Attempting to authenticate user: " + email);

        // Check if the provided password matches the stored password
        if (!checkPassword(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        // Generate and return the JWT token
        return jwtTokenProvider.generateToken(user);
    }

    /**
     * Retrieves a user by email.
     *
     * @param email The user's email.
     * @return The User object.
     * @throws InvalidCredentialsException If the user is not found.
     */
    public User findByEmail(String email) throws InvalidCredentialsException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email"));
    }

    /**
     * Checks if the raw password matches the encoded password.
     *
     * @param rawPassword     The raw password input.
     * @param encodedPassword The encoded password stored in the database.
     * @return True if passwords match, false otherwise.
     */
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Loads the user by email.
     *
     * @param email The user's email.
     * @return UserDetails object.
     * @throws UsernameNotFoundException If user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = null;
        try {
            user = findByEmail(email);
        } catch (InvalidCredentialsException e) {
            throw new RuntimeException(e);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                getAuthorities(user)
        );
    }

    /**
     * Converts user roles to GrantedAuthority collection.
     *
     * @param user The user entity.
     * @return Collection of GrantedAuthority.
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    public User signUp(String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        String encodedPassword = passwordEncoder.encode(password);

        Set<String> roles = new HashSet<>();
        roles.add("USER"); // Assign default role

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRoles(roles);
        return userRepository.save(user);
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
        // Logic for handling logout (e.g., invalidating a token) can be added here
    }
}


//package com.onlineshop.shop.authentication.services;
//
//import com.onlineshop.shop.authentication.exceptions.InvalidCredentialsException;
//import com.onlineshop.shop.authentication.models.BaseModel;
//import com.onlineshop.shop.authentication.models.User;
//import com.onlineshop.shop.authentication.repositories.UserRepository;
//import com.onlineshop.shop.authentication.security.JwtTokenProvider;
//import com.onlineshop.shop.notification.dtos.EmailFormat;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Service
//public class UserService extends BaseModel implements UserDetailsService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtTokenProvider jwtTokenProvider;
//    @Value("${JWT_SECRET}")
//    private String jwtSecret; // Injected JWT secret
//
//    @Autowired
//    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.jwtTokenProvider = jwtTokenProvider;
//    }
//
//    public String login(String email, String password) throws InvalidCredentialsException {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new InvalidCredentialsException("Invalid email"));
//
//        System.out.println("login called" + email + " password " + password + " user " + user);
//
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            throw new InvalidCredentialsException("Invalid password");
//        }
//
//        return jwtTokenProvider.generateToken(user);
//    }
//
//
//
//    /**
//     * Loads the user by email.
//     *
//     * @param email The user's email.
//     * @return UserDetails object.
//     * @throws UsernameNotFoundException If user is not found.
//     */
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
//        return new org.springframework.security.core.userdetails.User(
//                user.getEmail(),
//                user.getPassword(),
//                user.getAuthorities()
//        );
//    }
//
//    /**
//     * Authenticates the user and generates a JWT token.
//     *
//     * @param email    The user's email.
//     * @param password The user's password.
//     * @return A JWT token.
//     * @throws InvalidCredentialsException If authentication fails.
//     */
//
//    public User signUp(String name, String email, String password) {
//        if (userRepository.findByEmail(email).isPresent()) {
//            throw new IllegalArgumentException("Email already in use");
//        }
//
//        String encodedPassword = passwordEncoder.encode(password);
//
//        Set<String> roles = new HashSet<>();
//        roles.add("USER"); // Assign default role
//
//        User user = new User();
//        user.setEmail(email);
//        user.setPassword(encodedPassword);
//        user.setRoles(roles);
//        return userRepository.save(user);
////        try {
////            kafkaTemplate.send("sendEmail", objectMapper.writeValueAsString(getMessage(user))).get();
////            return userRepository.save(user);
////        } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
////            throw new RuntimeException("Failed to send Kafka message", e);
////        }
//    }
//
//    public EmailFormat getMessage(User user) {
//        EmailFormat message = new EmailFormat();
//        message.setTo(user.getEmail());
//        message.setFrom("geekysanjay@gmail.com");
//        message.setContent("Successfully signed up");
//        message.setSubject("Sign up success");
//        return message;
//    }
//
//    public void logout(String token) {
////        Optional<Token> optionalToken = tokenRepository.findByValueAndIsDeletedEquals(token, false);
////        if (optionalToken.isEmpty()) {
////            return;
////        }
////        optionalToken.get().setDeleted(true);
////        tokenRepository.save(optionalToken.get());
//    }
//}
