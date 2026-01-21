package com.tienda.electronicos.repository;

import com.tienda.electronicos.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    boolean existsByFilename(String filename);
}

