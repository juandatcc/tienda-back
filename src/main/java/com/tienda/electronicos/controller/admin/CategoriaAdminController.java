package com.tienda.electronicos.controller.admin;

import com.tienda.electronicos.dto.admin.CategoriaAdminResponse;
import com.tienda.electronicos.dto.category.CategoriaRequest;
import com.tienda.electronicos.service.CategoriaProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categorias")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CategoriaAdminController {

    private final CategoriaProductoService categoriaService;

    // ===== CREAR =====
    @PostMapping
    public ResponseEntity<CategoriaAdminResponse> crearCategoria(
            @RequestBody @Valid CategoriaRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoriaService.crearCategoria(request));
    }

    // ===== ACTUALIZAR =====
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoriaAdminResponse actualizarCategoria(
            @PathVariable("id") Long id,
            @RequestBody @Valid CategoriaRequest request
    ) {
        return categoriaService.actualizarCategoria(id, request);
    }


    // ===== ELIMINAR =====
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable("id") Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }


}
