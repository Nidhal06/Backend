package com.coworking.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class InvoiceStorageConfig {
    
    @Bean
    @ConfigurationProperties(prefix = "invoice.storage")
    public InvoiceStorageProperties invoiceStorageProperties() {
        return new InvoiceStorageProperties();
    }
    
    @Bean
    public Path invoiceStorageLocation(InvoiceStorageProperties properties) {
        return Paths.get(properties.getLocation())
                   .toAbsolutePath()
                   .normalize();
    }
}