package com.divitbui.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.divitbui.model.Pokemon;
import com.divitbui.model.PokemonsResponse;
import com.divitbui.service.PokemonService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@RequestMapping("/pokemons")
@RequiredArgsConstructor
public class PokemonController {

    private final PokemonService pokemonService;

    @GetMapping
    public Mono<PokemonsResponse> getPokemonList() {
        return pokemonService.fetchPokemons();
    }

    @GetMapping("/{pokemonId}")
    public Mono<Pokemon> getPokemonById(final Integer pokemonId) {
        return pokemonService.fetchPokemonById(pokemonId);
    }

}
