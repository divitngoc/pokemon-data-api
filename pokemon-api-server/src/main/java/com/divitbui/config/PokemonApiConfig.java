package com.divitbui.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "pokemon")
public class PokemonApiConfig {

    private String baseUrl;
    private String pokemonEndpoint;
    
}
