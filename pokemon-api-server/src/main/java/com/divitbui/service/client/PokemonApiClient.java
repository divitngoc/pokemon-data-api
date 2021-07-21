package com.divitbui.service.client;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.divitbui.config.PokemonApiConfig;
import com.divitbui.model.client.PokemonClientResponse;
import com.divitbui.model.client.PokemonsClientResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class PokemonApiClient {

    private final PokemonApiConfig config;
    private final WebClient client;
    private final ObjectMapper mapper;

    public Mono<PokemonsClientResponse> fetchPokemons() {
        log.debug("Fetching pokemons from endpoint: {}", config.getBaseUrl() + config.getPokemonEndpoint());
        return client.get()
                     .uri(config.getPokemonEndpoint() + "?limit=1")
                     .exchangeToMono(responseHandler -> responseHandler.bodyToMono(String.class))
                     .map(rawData -> {
                         log.debug("Successfully retrieve pokemons data: {}", rawData);
                         return mapClientResponse(rawData, PokemonsClientResponse.class);
                     });
    }

    public Mono<PokemonClientResponse> fetchPokemonById(Integer pokemonId) {
        final String endpoint = config.getBaseUrl() + config.getPokemonEndpoint() + "/" + pokemonId;
        log.debug("Fetching pokemon from endpoint: {}", endpoint);
        return client.get()
                     .uri(config.getPokemonEndpoint() + "/" + pokemonId)
                     .exchangeToMono(responseHandler -> responseHandler.bodyToMono(String.class))
                     .map(rawData -> {
                         log.debug("Successfully retrieve pokemon with id {}", pokemonId);
                         return mapClientResponse(rawData, PokemonClientResponse.class);
                     });
    }

    private <T> T mapClientResponse(String rawData, Class<T> clazz) {
        try {
            return mapper.readValue(rawData, clazz);
        } catch (JsonProcessingException e) {
            log.error("Unable to deserialize pokeapi data", e);
            throw new RuntimeException(e);
        }
    }

}
