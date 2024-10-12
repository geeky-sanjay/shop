package com.onlineshop.shop.authentication.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlineshop.shop.authentication.security.JwtTokenProvider;
import com.onlineshop.shop.authentication.security.JwtTokenValidator;
import com.onlineshop.shop.authentication.security.filter.CustomAuthenticationFilter;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
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

    @Autowired
    public SecurityConfig(@Lazy UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Value("${JWT_EXPIRATION_MS}")
    private long jwtExpirationMs;

    private static final String LOGIN_URL = "/login";
    private static final String SIGNUP_URL = "/signup";
    private static final String PRODUCTS_URL = "/products";
    private static final String CATEGORIES_URL = "/categories";

    // PasswordEncoder Bean
    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager Bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // JwtTokenProvider Bean (already annotated with @Component, so no need to define here)
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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
        config.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // Apply the default OAuth2 Authorization Server configuration
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        // Configure exception handling
        http.exceptionHandling(exceptions ->
                        exceptions.defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint(LOGIN_URL),
                                new org.springframework.security.web.util.matcher.MediaTypeRequestMatcher(MediaType.TEXT_HTML))
                )
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(Customizer.withDefaults())
                );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http, AuthenticationManager authManager, JwtTokenValidator jwtTokenValidator) throws Exception {
        // Create and configure the custom authentication filter
        CustomAuthenticationFilter customAuthFilter = new CustomAuthenticationFilter(LOGIN_URL, authManager, userService, objectMapper);
        customAuthFilter.setFilterProcessesUrl(LOGIN_URL);

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
                // Add the custom authentication filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(customAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // Add the JWT token validator after the custom authentication filter
                .addFilterAfter(jwtTokenValidator, CustomAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(jwtSecret.getBytes(), "HMACSHA512")).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        authoritiesConverter.setAuthoritiesClaimName("roles");
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }

    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .formLogin(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }
}
