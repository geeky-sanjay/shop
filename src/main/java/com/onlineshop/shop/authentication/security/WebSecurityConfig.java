package com.onlineshop.shop.authentication.security;

import com.onlineshop.shop.authentication.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.onlineshop.shop.authentication.security.jwt.AuthEntryPointJwt;
import com.onlineshop.shop.authentication.security.services.UserDetailsServiceImpl;

@Configuration
@EnableMethodSecurity
// (securedEnabled = true,
// jsr250Enabled = true,
// prePostEnabled = true) // by default
public class WebSecurityConfig { // extends WebSecurityConfigurerAdapter {
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
    private static final String LOGIN_URL = "/auth/login";
    private static final String SIGNUP_URL = "/signup";
    private static final String PRODUCTS_URL = "/products";
    private static final String CATEGORIES_URL = "/categories";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll() // It will cover all public auth urls
                                .requestMatchers("/api/test/**").permitAll()
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
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
