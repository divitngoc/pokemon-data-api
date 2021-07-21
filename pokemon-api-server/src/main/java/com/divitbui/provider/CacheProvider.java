package com.divitbui.provider;

import java.util.Optional;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.divitbui.config.CacheConfig.CacheType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CacheProvider {

    private final CacheManager cacheManager;

    public Cache getCache(CacheType type) {
        return Optional.ofNullable(cacheManager.getCache(type.name()))
                       .orElseThrow(() -> new RuntimeException("Unable to find client cache with CacheType: " + type.name()));
    }

}
