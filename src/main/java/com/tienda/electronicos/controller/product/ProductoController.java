
package com.tienda.electronicos.controller.product;

import com.tienda.electronicos.dto.product.ProductoAdminResponse;
import com.tienda.electronicos.dto.product.ProductoRequest;
import com.tienda.electronicos.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // ===== LISTAR (ADMIN / USER) =====
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public List<ProductoAdminResponse> listarProductos() {
        return productoService.listarProductos();
    }

    // ===== OBTENER POR ID =====
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ProductoAdminResponse obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id);
    }

    // ===== CREAR (ADMIN) =====
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoAdminResponse> crearProducto(
            @RequestBody @Valid ProductoRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.crearProducto(request));
    }

    // ===== ACTUALIZAR (ADMIN) =====
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductoAdminResponse actualizarProducto(
            @PathVariable("id") Long id,
            @RequestBody @Valid ProductoRequest request
    ) {
        return productoService.actualizarProducto(id, request);
    }

    // ===== ELIMINAR (ADMIN) =====
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarProducto(@PathVariable("id") Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
