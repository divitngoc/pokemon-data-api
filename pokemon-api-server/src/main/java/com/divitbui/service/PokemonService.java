package com.divitbui.service;

import java.util.Optional;

import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.divitbui.config.CacheConfig.CacheType;
import com.divitbui.model.Pokemon;
import com.divitbui.model.PokemonsResponse;
import com.divitbui.model.client.PokemonClientResponse;
import com.divitbui.model.client.PokemonsClientResponse;
import com.divitbui.provider.CacheProvider;
import com.divitbui.service.client.PokemonApiClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class PokemonService {

    private final PokemonApiClient pokemonApiClient;
    private final CacheProvider cacheProvider;

    public Mono<PokemonsResponse> fetchPokemons() {
        Cache cache = cacheProvider.getCache(CacheType.POKEMONS_CLIENT_RESPONSE);
        return Optional.ofNullable(cache.get("fetchPokemons", PokemonsClientResponse.class))
                       .map(response -> {
                           log.debug("Found pokemons in cache.");
                           return Mono.just(response);
                       })
                       .orElseGet(() -> pokemonApiClient.fetchPokemons()
                                                        .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException())))
                                                        .doOnSuccess(clientResponse -> {
                                                            log.debug("Retrieved list of pokemons, adding pokemons client response to cache");
                                                            cache.put("fetchPokemons", clientResponse);
                                                        }))
                       .flatMapMany(cr -> Flux.fromIterable(cr.getResults()))
                       .parallel() 
                       .runOn(Schedulers.boundedElastic())
                       .flatMap(namedApiResource -> fetchPokemon(namedApiResource.getUrl()))
                       .sequential()
                       .collectList()
                       .map(pokemons -> PokemonsResponse.builder().pokemons(pokemons).build());
    }

    public Mono<Pokemon> fetchPokemonById(Integer pokemonId) {
        Cache cache = cacheProvider.getCache(CacheType.POKEMON_CLIENT_RESPONSE);
        return Optional.ofNullable(cache.get(pokemonId, PokemonClientResponse.class))
                       .map(response -> {
                           log.debug("Found pokemon with id {} in cache.", pokemonId);
                           return Mono.just(response);
                       })
                       .orElseGet(() -> pokemonApiClient.fetchPokemonById(pokemonId)
                                                        .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException())))
                                                        .doOnSuccess(clientResponse -> {
                                                            log.debug("Retrieved pokemon with id {}, adding pokemon client response to cache",
                                                                      pokemonId);
                                                            cache.put(pokemonId, clientResponse);
                                                        }))
                       .map(this::pokemonClientResponseToPokemon);
    }

    private Mono<Pokemon> fetchPokemon(String url) {
        String pokemonId = UriComponentsBuilder.fromUriString(url)
                                               .build()
                                               .getPathSegments()
                                               .stream()
                                               .reduce((p1, p2) -> p2)
                                               .orElseThrow();

        return fetchPokemonById(Integer.valueOf(pokemonId));
    }

    private Pokemon pokemonClientResponseToPokemon(PokemonClientResponse clientResponse) {
        return Pokemon.builder()
                      .id(clientResponse.getId())
                      .name(clientResponse.getName())
                      .height(clientResponse.getHeight())
                      .weight(clientResponse.getWeight())
                      .build();
    }
}
