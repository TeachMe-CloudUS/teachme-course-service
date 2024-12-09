package us.cloud.teachme.courseservice.service;
import java.util.Map;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;

@Service
public class CacheInspector {

    private final CacheManager cacheManager;

    public CacheInspector(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public Map<Object, Object> getCacheContents(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache instanceof CaffeineCache caffeineCache) {
            com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                    caffeineCache.getNativeCache();
            return nativeCache.asMap(); // Devuelve el contenido como un mapa
        }
        throw new IllegalArgumentException("Caché no encontrada o no es una instancia de Caffeine.");
    }
    
}
