package com.mobilewebservices.cache.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cache")
@Tag(name = "Cache Management", description = "API endpoints for cache management and monitoring")
// @SecurityRequirement(name = "Bearer Authentication") // Disabled - no authentication
public class CacheController {

    private final CacheManager cacheManager;

    public CacheController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @GetMapping("/names")
    @Operation(summary = "Get cache names", description = "Retrieve all available cache names")
    public ResponseEntity<Collection<String>> getCacheNames() {
        return ResponseEntity.ok(cacheManager.getCacheNames());
    }

    @GetMapping("/stats")
    @Operation(summary = "Get cache statistics", description = "Retrieve cache statistics for monitoring")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();

        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                Map<String, Object> cacheStats = new HashMap<>();
                cacheStats.put("name", cacheName);
                cacheStats.put("nativeCache", cache.getNativeCache().getClass().getSimpleName());

                // Try to get Caffeine-specific stats
                if (cache.getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache) {
                    var caffeineCache = (com.github.benmanes.caffeine.cache.Cache<?, ?>) cache.getNativeCache();
                    var caffeineStats = caffeineCache.stats();

                    cacheStats.put("hitCount", caffeineStats.hitCount());
                    cacheStats.put("missCount", caffeineStats.missCount());
                    cacheStats.put("hitRate", caffeineStats.hitRate());
                    cacheStats.put("evictionCount", caffeineStats.evictionCount());
                    cacheStats.put("estimatedSize", caffeineCache.estimatedSize());
                }

                stats.put(cacheName, cacheStats);
            }
        });

        return ResponseEntity.ok(stats);
    }

    @PostMapping("/clear/{cacheName}")
    @Operation(summary = "Clear specific cache", description = "Clear all entries from a specific cache")
    public ResponseEntity<String> clearCache(@PathVariable String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            return ResponseEntity.ok("Cache '" + cacheName + "' cleared successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/clear-all")
    @Operation(summary = "Clear all caches", description = "Clear all entries from all caches")
    public ResponseEntity<String> clearAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
        return ResponseEntity.ok("All caches cleared successfully");
    }

    @PostMapping("/evict/{cacheName}/{key}")
    @Operation(summary = "Evict cache entry", description = "Remove a specific entry from cache")
    public ResponseEntity<String> evictCacheEntry(@PathVariable String cacheName, @PathVariable String key) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            return ResponseEntity.ok("Cache entry '" + key + "' evicted from '" + cacheName + "'");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
