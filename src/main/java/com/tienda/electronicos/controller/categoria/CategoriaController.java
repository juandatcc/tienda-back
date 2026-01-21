package com.tienda.electronicos.controller.categoria;

import com.tienda.electronicos.dto.admin.CategoriaAdminResponse;
import com.tienda.electronicos.service.CategoriaProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaProductoService categoriaService;

    @GetMapping
    public List<CategoriaAdminResponse> listarCategorias() {
        return categoriaService.listarCategorias();
    }

    @GetMapping("/{id}")
    public CategoriaAdminResponse obtenerPorId(@PathVariable Long id) {
        return categoriaService.obtenerPorId(id);
    }
}
