package com.tienda.electronicos.service;

import com.tienda.electronicos.entity.Asset;
import com.tienda.electronicos.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.*;
import java.time.OffsetDateTime;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AssetImportService {

    private final AssetRepository assetRepository;

    @Value("${frontend.assets.path:frontend/assets}")
    private String assetsPath;

    @Transactional
    public int importAllAssets() throws IOException {
        Path base = Paths.get(assetsPath).toAbsolutePath().normalize();
        if (!Files.exists(base) || !Files.isDirectory(base)) {
            throw new IOException("Ruta de assets no existe: " + base);
        }

        try (Stream<Path> paths = Files.walk(base)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        try {
                            byte[] content = Files.readAllBytes(path);
                            Asset a = Asset.builder()
                                    .filename(path.getFileName().toString())
                                    .path(base.relativize(path).toString())
                                    .content(content)
                                    .contentType(Files.probeContentType(path))
                                    .size((long) content.length)
                                    .createdAt(OffsetDateTime.now())
                                    .build();

                            if (!assetRepository.existsByFilename(a.getFilename())) {
                                assetRepository.save(a);
                                return 1;
                            }
                            return 0;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .mapToInt(Integer::intValue)
                    .sum();
        }
    }
}
