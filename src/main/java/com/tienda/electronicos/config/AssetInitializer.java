package com.tienda.electronicos.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class AssetInitializer {

    private static final Logger log = LoggerFactory.getLogger(AssetInitializer.class);

    @Value("${frontend.assets.path:frontend/assets}")
    private String assetsPath;

    @PostConstruct
    public void init() {
        try {
            Path base = Path.of(assetsPath).toAbsolutePath().normalize();
            Path products = base.resolve("products");
            if (!Files.exists(products)) {
                Files.createDirectories(products);
                log.info("Carpeta de assets creada: {}", products);
            } else {
                log.info("Carpeta de assets ya existe: {}", products);
            }
        } catch (IOException e) {
            log.warn("No se pudo crear la carpeta de assets: {} - {}", assetsPath, e.getMessage());
        }
    }
}

