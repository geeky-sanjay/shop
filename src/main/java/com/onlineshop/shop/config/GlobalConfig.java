package com.onlineshop.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class GlobalConfig {
    @Bean
    public RestTemplate getController(){
        return new RestTemplate();
    }
}


