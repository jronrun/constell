package com.benayn.constell.services.capricorn.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    protected enum Caches {

        ACCOUNT("accounts", 300, 50),
        ROLE("roles", 300, 100),
        PERMISSION("permissions", 300, 100)
        ;

        private final String name;
        private final int secondsToExpire;
        private final int maximumSize;
    }

    @Bean
    public CacheManager cacheManager(Ticker ticker) {
        Collection<? extends Cache> caches = Arrays.stream(Caches.values())
            .map(cache -> buildCache(cache.getName(), ticker, cache.getSecondsToExpire(), cache.getMaximumSize()))
            .collect(Collectors.toList())
            ;

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(caches);
        return manager;
    }

    private CaffeineCache buildCache(String name, Ticker ticker, int secondsToExpire, int maximumSize) {
        return new CaffeineCache(name, Caffeine.newBuilder()
            .expireAfterWrite(secondsToExpire, TimeUnit.SECONDS)
            .maximumSize(maximumSize)
            .ticker(ticker)
            .build()
        );
    }

    @Bean
    public Ticker ticker() {
        return Ticker.systemTicker();
    }
}
