package com.cptrans.petrocarga.infrastructure.configs;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "dashboard-kpi",
            "dashboard-vehicle-types",
            "dashboard-districts",
            "dashboard-origins",
            "dashboard-summary"
        );
    }
}
