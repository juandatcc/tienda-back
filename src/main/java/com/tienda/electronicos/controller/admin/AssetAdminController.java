package com.tienda.electronicos.controller.admin;

import com.tienda.electronicos.entity.Asset;
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
    public ResponseEntity<List<Asset>> listarAssets() {
        return ResponseEntity.ok(assetRepository.findAll());
    }
}
