package com.tienda.electronicos.service;

import com.tienda.electronicos.dto.admin.CategoriaAdminResponse;
import com.tienda.electronicos.dto.category.CategoriaRequest;
import com.tienda.electronicos.entity.CategoriaProducto;
import com.tienda.electronicos.repository.CategoriaProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaProductoService {

    private final CategoriaProductoRepository categoriaRepository;

    // ===== LISTAR =====
    @Transactional(readOnly = true)
    public List<CategoriaAdminResponse> listarCategorias() {
        return StreamSupport.stream(categoriaRepository.findAll().spliterator(), false)
                .map(this::mapToResponse)
                .toList();
    }

    // ===== OBTENER POR ID =====
    @Transactional(readOnly = true)
    public CategoriaAdminResponse obtenerPorId(Long id) {
        CategoriaProducto categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoría no existe"
                ));
        return mapToResponse(categoria);
    }

    // ===== CREAR =====
    public CategoriaAdminResponse crearCategoria(CategoriaRequest request) {

        //
        if (categoriaRepository.existsByNombre(request.getNombre())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una categoría con ese nombre"
            );
        }

        // Crear y guardar la nueva categoría
        CategoriaProducto categoria = new CategoriaProducto();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        // Guardar en la base de datos
        categoriaRepository.save(categoria);
        return mapToResponse(categoria);
    }

    // ===== ACTUALIZAR =====
    public CategoriaAdminResponse actualizarCategoria(Long id, CategoriaRequest request) {

        // Buscar la categoría existente
        CategoriaProducto categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoría no existe"
                ));

        // Verificar si el nuevo nombre ya está en uso por otra categoría
        if (categoriaRepository.existsByNombre(request.getNombre())
                && !categoria.getNombre().equalsIgnoreCase(request.getNombre())) {

            // Si existe otra categoría con el mismo nombre, lanzar excepción
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una categoría con ese nombre"
            );
        }

        // Actualizar los campos de la categoría
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        // Guardar los cambios en la base de datos
        categoriaRepository.save(categoria);
        return mapToResponse(categoria);
    }

    // ===== ELIMINAR =====
    public void eliminarCategoria(Long id) {
        // Verificar si la categoría existe
        if (!categoriaRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Categoría no existe"
            );
        }
        // Eliminar la categoría por su ID
        categoriaRepository.deleteById(id);
    }

    // ===== MAPPER =====
    private CategoriaAdminResponse mapToResponse(CategoriaProducto c) {
        // Mapear entidad CategoriaProducto a DTO CategoriaAdminResponse
        return new CategoriaAdminResponse(
                c.getIdCategoria(),
                c.getNombre(),
                c.getDescripcion()
        );
    }
}
