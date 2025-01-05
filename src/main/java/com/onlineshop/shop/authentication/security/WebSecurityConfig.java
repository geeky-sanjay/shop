package com.onlineshop.shop.authentication.security;

import com.onlineshop.shop.authentication.security.jwt.AuthTokenFilter;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.onlineshop.shop.authentication.security.jwt.AuthEntryPointJwt;
import com.onlineshop.shop.authentication.security.services.UserDetailsServiceImpl;

@Configuration
// (securedEnabled = true,
// jsr250Enabled = true,
// prePostEnabled = true) // by default
@EnableWebSecurity(debug = true)
public class WebSecurityConfig { // extends WebSecurityConfigureAdapter {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // URLS
    // Inject the property value
    @Value("${api_prefix}")
    private String apiPrefix;

    // Instance fields for URLs
    private String authUrl;
    private String productsUrl;
    private String categoriesUrl;
    private String cartItemUrl;
    private String cartUrl;
    // Initialize URLs after apiPrefix is injected
    @PostConstruct
    public void init() {
        this.authUrl = apiPrefix + "/auth";
        this.productsUrl = apiPrefix + "/products";
        this.categoriesUrl = apiPrefix + "/categories";
        this.cartItemUrl = apiPrefix + "/cartItems";
        this.cartUrl = apiPrefix + "/cart";
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(authUrl + "/**").permitAll() // It will cover all public auth urls
                                .requestMatchers("/api/**").permitAll()
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                                .requestMatchers(HttpMethod.GET, productsUrl + "/**").permitAll()
//                                .requestMatchers(HttpMethod.GET,  cartUrl + "/**").permitAll()
//                                .requestMatchers(HttpMethod.GET,  cartItemUrl + "/**").permitAll()
//                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                                .requestMatchers(HttpMethod.POST, productsUrl).authenticated()
//                                .requestMatchers(HttpMethod.PUT, productsUrl + "/**").authenticated()
//                                .requestMatchers(HttpMethod.DELETE, productsUrl + "/**").authenticated()
//                                .requestMatchers(HttpMethod.GET, categoriesUrl + "/**").permitAll()
//                                .requestMatchers(HttpMethod.POST, categoriesUrl).authenticated()
//                                .requestMatchers(HttpMethod.PUT, categoriesUrl + "/**").authenticated()
//                                .requestMatchers(HttpMethod.DELETE, categoriesUrl + "/**").authenticated()
                                    .anyRequest().permitAll()
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
