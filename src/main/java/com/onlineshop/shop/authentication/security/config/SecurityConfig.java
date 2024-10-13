package com.onlineshop.shop.authentication.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlineshop.shop.authentication.security.JwtTokenProvider;
import com.onlineshop.shop.authentication.security.JwtTokenValidator;
import com.onlineshop.shop.authentication.security.CustomAuthenticationProvider;
import com.onlineshop.shop.authentication.services.CustomUserDetailsService;
import com.onlineshop.shop.authentication.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityConfig(@Lazy UserService userService, ObjectMapper objectMapper, CustomUserDetailsService customUserDetailsService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Value("${JWT_EXPIRATION_MS}")
    private long jwtExpirationMs;

    private static final String LOGIN_URL = "/login";
    private static final String SIGNUP_URL = "/signup";
    private static final String PRODUCTS_URL = "/products";
    private static final String CATEGORIES_URL = "/categories";



    // AuthenticationManager Bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Custom Authentication Provider Bean
    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider();
    }

    // JwtTokenValidator Bean
    @Bean
    public JwtTokenValidator jwtTokenValidator(AuthenticationManager authenticationManager) {
        return new JwtTokenValidator(authenticationManager, jwtTokenProvider);
    }

    // CORS Filter Bean
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Adjust as needed
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    // API Security Filter Chain
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http, AuthenticationManager authManager, JwtTokenValidator jwtTokenValidator) throws Exception {
        // Register the custom AuthenticationProvider
        http.authenticationProvider(customAuthenticationProvider());

        http
                .cors(Customizer.withDefaults()) // Apply CORS configuration
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, SIGNUP_URL).permitAll()
                        .requestMatchers(HttpMethod.POST, LOGIN_URL).permitAll()
                        .requestMatchers(HttpMethod.GET, PRODUCTS_URL, PRODUCTS_URL + "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, PRODUCTS_URL).authenticated()
                        .requestMatchers(HttpMethod.PUT, PRODUCTS_URL + "/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, PRODUCTS_URL + "/**").authenticated()
                        .requestMatchers(HttpMethod.GET, CATEGORIES_URL, CATEGORIES_URL + "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, CATEGORIES_URL).authenticated()
                        .requestMatchers(HttpMethod.PUT, CATEGORIES_URL + "/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, CATEGORIES_URL + "/**").authenticated()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                // Add the JWT token validator after the authentication process
                .addFilterAfter(jwtTokenValidator, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for APIs
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    // Default Security Filter Chain (lowest priority)
    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .formLogin(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // JWT Decoder Bean
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(jwtSecret.getBytes(), "HMACSHA512")).build();
    }

    // JWT Authentication Converter Bean
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        authoritiesConverter.setAuthoritiesClaimName("roles");
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }
}
