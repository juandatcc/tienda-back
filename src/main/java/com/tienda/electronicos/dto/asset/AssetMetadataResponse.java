package com.tienda.electronicos.dto.asset;

import java.time.OffsetDateTime;

// Metadatos de un asset (sin el contenido binario)
public record AssetMetadataResponse(
        String id,
        String filename,
        String path,
        String contentType,
        Long size,
        OffsetDateTime createdAt
) {}
