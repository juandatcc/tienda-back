package com.tienda.electronicos.controller.admin;

import com.tienda.electronicos.dto.asset.AssetMetadataResponse;
import com.tienda.electronicos.service.AssetImportService;
import com.tienda.electronicos.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/assets")
@RequiredArgsConstructor
public class AssetAdminController {

    private final AssetImportService assetImportService;
    private final AssetRepository assetRepository;

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> importAssets() {
        try {
            int imported = assetImportService.importAllAssets();
            return ResponseEntity.ok("Importados: " + imported);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AssetMetadataResponse>> listarAssets() {
        // Solo metadatos: el BLOB no se serializa
        return ResponseEntity.ok(assetRepository.findAll().stream()
                .map(a -> new AssetMetadataResponse(
                        a.getId(),
                        a.getFilename(),
                        a.getPath(),
                        a.getContentType(),
                        a.getSize(),
                        a.getCreatedAt()))
                .toList());
    }
}
