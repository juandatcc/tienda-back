package com.tienda.electronicos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

// Configuración para servir recursos estáticos desde una ruta personalizada
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Ruta de los assets configurada en application.properties
    @Value("${frontend.assets.path:frontend/assets}")
    private String assetsPath;

    // Configura los manejadores de recursos para servir archivos estáticos
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Convertir a ruta absoluta y normalizar separadores para file: URI
        Path path = Path.of(assetsPath).toAbsolutePath().normalize();
        String assetsLocation = "file:" + path.toString().replace("\\", "/") + "/";

        // Registrar el manejador de recursos para la ruta /assets/**
        registry.addResourceHandler("/assets/**")
                .addResourceLocations(assetsLocation)
                .setCachePeriod(3600);
    }
}

