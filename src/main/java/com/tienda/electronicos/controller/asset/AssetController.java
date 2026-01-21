package com.tienda.electronicos.controller.asset;

import com.tienda.electronicos.entity.Asset;
import com.tienda.electronicos.repository.AssetRepository;
import com.tienda.electronicos.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor

// Controlador público para servir archivos almacenados como Assets
public class AssetController {
    // Repositorio de Assets
    private final AssetRepository assetRepository;

    // Endpoint para obtener un Asset por su ID
    @GetMapping("/{id}")
    // Devuelve el archivo con el tipo de contenido adecuado
    public ResponseEntity<byte[]> getAsset(@PathVariable Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));
        // Construye la respuesta HTTP con los encabezados adecuados
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + asset.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(asset.getContentType() != null ? asset.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .body(asset.getContent());
    }
}