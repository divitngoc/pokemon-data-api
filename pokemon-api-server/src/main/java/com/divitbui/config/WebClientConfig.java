package com.divitbui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final PokemonApiConfig config;
    
    @Bean
    public WebClient webClient() {
        return WebClient.create(config.getBaseUrl());
    }
     
}
