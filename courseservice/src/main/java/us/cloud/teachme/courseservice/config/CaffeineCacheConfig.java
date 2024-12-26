package us.cloud.teachme.courseservice.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;



@Configuration
public class CaffeineCacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .maximumSize(100) // Máximo de 100 entradas
                .expireAfterWrite(10, TimeUnit.MINUTES) // Expiración tras 10 minutos
                .recordStats(); // Habilitar métricas
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);
        cacheManager.setAllowNullValues(false); // Opcional, evita almacenar valores nulos
        return cacheManager;
    }
    
}
