package com.mobilewebservices.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // Configure default cache settings
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(30))
                .recordStats());

        // Define specific cache configurations
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "announcements",
            "articles",
            "posts",
            "events",
            "food-menus",
            "sliders",
            "latest-sliders",
            "sliders-last-month",
            "latest-announcements",
            "latest-articles",
            "latest-posts",
            "latest-events",
            "usefulLinks",
            "configVersion"
        ));

        return cacheManager;
    }

    @Bean("announcementsCacheManager")
    public CacheManager announcementsCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("announcements");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(50)
                .maximumSize(500)
                .expireAfterWrite(Duration.ofMinutes(15)) // Shorter cache for dynamic content
                .recordStats());
        return cacheManager;
    }

    @Bean("foodMenuCacheManager")
    public CacheManager foodMenuCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("food-menus");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(20)
                .maximumSize(100)
                .expireAfterWrite(Duration.ofHours(2)) // Longer cache for relatively static content
                .recordStats());
        return cacheManager;
    }

    @Bean("eventsCacheManager")
    public CacheManager eventsCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("events");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(30)
                .maximumSize(300)
                .expireAfterWrite(Duration.ofMinutes(45))
                .recordStats());
        return cacheManager;
    }

    @Bean("postsCacheManager")
    public CacheManager postsCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("posts");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(40)
                .maximumSize(400)
                .expireAfterWrite(Duration.ofMinutes(20))
                .recordStats());
        return cacheManager;
    }
}
