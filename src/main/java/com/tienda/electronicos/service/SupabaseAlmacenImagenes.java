package com.tienda.electronicos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Guarda las imágenes en el bucket público de Supabase Storage. La clave secreta
 * vive SOLO en el servidor (SUPABASE_SECRET_KEY o secrets.properties): el navegador
 * nunca la ve ni escribe directo en el bucket.
 *
 * Si falta la clave, la API arranca igual y la subida responde 503 hasta configurarla.
 */
@Component
public class SupabaseAlmacenImagenes implements AlmacenImagenes {

    private static final Logger log = LoggerFactory.getLogger(SupabaseAlmacenImagenes.class);

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final String baseUrl;
    private final String claveSecreta;
    private final String bucket;

    public SupabaseAlmacenImagenes(
            @Value("${supabase.url}") String url,
            @Value("${supabase.secret-key:}") String claveSecreta,
            @Value("${supabase.storage.bucket:productos}") String bucket
    ) {
        this.baseUrl = url.replaceAll("/+$", "");
        this.claveSecreta = claveSecreta;
        this.bucket = bucket;
        if (claveSecreta == null || claveSecreta.isBlank()) {
            log.warn("supabase.secret-key no configurada: la subida de imágenes responderá 503 hasta definir SUPABASE_SECRET_KEY o secrets.properties");
        }
    }

    @Override
    public String guardar(String ruta, String tipoContenido, byte[] contenido) {
        if (claveSecreta == null || claveSecreta.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "La subida de imágenes no está configurada en el servidor");
        }

        try {
            HttpRequest.Builder request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/storage/v1/object/" + bucket + "/" + ruta))
                    .timeout(Duration.ofSeconds(30))
                    // Las claves nuevas (sb_secret_...) no son JWT: solo van en la cabecera apikey
                    .header("apikey", claveSecreta)
                    .header("Content-Type", tipoContenido)
                    .header("x-upsert", "false")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(contenido));
            if (claveSecreta.startsWith("eyJ")) {
                // Clave legacy (JWT): además va como Bearer
                request.header("Authorization", "Bearer " + claveSecreta);
            }

            HttpResponse<String> response = http.send(request.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                log.error("Supabase Storage respondió {} al subir {}: {}", response.statusCode(), ruta, response.body());
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "No se pudo guardar la imagen en el almacenamiento");
            }
        } catch (IOException e) {
            log.error("Error de conexión con Supabase Storage al subir {}", ruta, e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "No se pudo conectar con el almacenamiento de imágenes");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Subida de imagen interrumpida");
        }

        String urlPublica = baseUrl + "/storage/v1/object/public/" + bucket + "/" + ruta;
        log.info("Imagen subida a Supabase: {}", urlPublica);
        return urlPublica;
    }
}
