package com.divitbui.config;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;

@Configuration(proxyBeanMethods = false)
@EnableCaching
public class CacheConfig {

    private static final int CACHE_SIZE = 1000;
    private static final int DAYS_TO_EXPIRE = 1;

    @Bean
    public CacheManager cacheManager(Ticker ticker) {
        final CaffeineCache pokemonClientCache = buildCache(CacheType.POKEMON_CLIENT_RESPONSE.name(), ticker, DAYS_TO_EXPIRE,
                                                            TimeUnit.DAYS);
        final CaffeineCache pokemonsClientCache = buildCache(CacheType.POKEMONS_CLIENT_RESPONSE.name(), ticker, DAYS_TO_EXPIRE,
                                                             TimeUnit.DAYS);

        final SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(pokemonClientCache, pokemonsClientCache));
        return manager;
    }

    private CaffeineCache buildCache(String name, Ticker ticker, int timeToExpire, TimeUnit timeUnit) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                                               .expireAfterWrite(timeToExpire, timeUnit)
                                               .maximumSize(CACHE_SIZE)
                                               .ticker(ticker)
                                               .build());
    }

    @Bean
    public Ticker ticker() {
        return Ticker.systemTicker();
    }

    public enum CacheType {
        POKEMON_CLIENT_RESPONSE,
        POKEMONS_CLIENT_RESPONSE,
    }

}
