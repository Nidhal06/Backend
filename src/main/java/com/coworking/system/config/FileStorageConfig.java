package com.coworking.system.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EnableConfigurationProperties(FileStorageProperties.class)
public class FileStorageConfig {
    
    private final FileStorageProperties properties;

    public FileStorageConfig(FileStorageProperties properties) {
        this.properties = properties;
    }
    
    @Bean
    public Path uploadLocation() {
        return Paths.get(properties.getUploadDir())
                   .toAbsolutePath()
                   .normalize();
    }
}