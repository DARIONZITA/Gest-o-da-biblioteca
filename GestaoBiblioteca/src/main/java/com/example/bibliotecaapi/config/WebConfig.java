package com.example.bibliotecaapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private FileStorageConfig fileStorageConfig;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Com server.servlet.context-path=/api, os patterns aqui NÃO incluem o /api
        // URL final exposta fica: /api/uploads/capas/**
        registry.addResourceHandler("/uploads/capas/**")
                .addResourceLocations("file:" + fileStorageConfig.getUploadDir() + "/")
                .setCachePeriod(3600);
    }
}
