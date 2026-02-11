package com.example.bibliotecaapi.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configuração do cache Caffeine para recomendações
     * - Expira após 2 horas (recomendações são relativamente estáticas)
     * - Máximo de 1000 entradas em memória (evita uso excessivo de RAM)
     * - Remoção baseada em LRU (Least Recently Used)
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("recomendacoes");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(2, TimeUnit.HOURS)
                .maximumSize(1000)
                .recordStats());
        return cacheManager;
    }
}
