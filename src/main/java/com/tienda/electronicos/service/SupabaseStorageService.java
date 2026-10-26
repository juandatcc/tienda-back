package com.tienda.electronicos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/**
 * Sube imágenes al bucket público de Supabase Storage y devuelve su URL pública.
 * La clave secreta solo vive en el backend (variable SUPABASE_SECRET_KEY o secrets.properties).
 */
@Service
public class SupabaseStorageService {

    private static final Logger log = LoggerFactory.getLogger(SupabaseStorageService.class);

    // Tipos de imagen permitidos y la extensión con la que se guardan
    private static final Map<String, String> EXTENSIONES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp",
            "image/gif", "gif"
    );

    private static final long TAMANO_MAXIMO = 5L * 1024 * 1024; // 5 MB

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final String supabaseUrl;
    private final String secretKey;
    private final String bucket;

    public SupabaseStorageService(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.secret-key:}") String secretKey,
            @Value("${supabase.storage.bucket:productos}") String bucket
    ) {
        this.supabaseUrl = supabaseUrl.replaceAll("/+$", "");
        this.secretKey = secretKey;
        this.bucket = bucket;
        if (secretKey == null || secretKey.isBlank()) {
            log.warn("supabase.secret-key no configurada: la subida de imágenes fallará hasta definir SUPABASE_SECRET_KEY o secrets.properties");
        }
    }

    /** Sube la imagen de un producto y devuelve la URL pública que se guarda en PRODUCTO.IMAGEN_URL. */
    public String subirImagenProducto(String idProducto, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Archivo vacío");
        }
        String extension = EXTENSIONES.get(file.getContentType());
        if (extension == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato no permitido: usa JPG, PNG, WEBP o GIF");
        }
        if (file.getSize() > TAMANO_MAXIMO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La imagen supera el máximo de 5 MB");
        }
        if (secretKey == null || secretKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Almacenamiento de imágenes no configurado");
        }

        // Nombre generado: nunca se usa el nombre original como ruta
        String ruta = "productos/" + idProducto + "/" + UUID.randomUUID() + "." + extension;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "/storage/v1/object/" + bucket + "/" + ruta))
                    .timeout(Duration.ofSeconds(30))
                    .header("apikey", secretKey)
                    .header("Content-Type", file.getContentType())
                    .header("x-upsert", "false")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                log.error("Supabase Storage respondió {} al subir {}: {}", response.statusCode(), ruta, response.body());
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo guardar la imagen en el almacenamiento");
            }
        } catch (IOException e) {
            log.error("Error de conexión con Supabase Storage al subir {}", ruta, e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo conectar con el almacenamiento de imágenes");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Subida de imagen interrumpida");
        }

        String urlPublica = supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + ruta;
        log.info("Imagen subida a Supabase: {}", urlPublica);
        return urlPublica;
    }
}
