package com.tienda.electronicos.service;

import com.tienda.electronicos.dto.product.ProductoAdminResponse;
import com.tienda.electronicos.dto.product.ProductoCreateRequest;
import com.tienda.electronicos.dto.product.ProductoRequest;
import com.tienda.electronicos.entity.Asset;
import com.tienda.electronicos.entity.CategoriaProducto;
import com.tienda.electronicos.entity.Producto;
import com.tienda.electronicos.repository.AssetRepository;
import com.tienda.electronicos.repository.CategoriaProductoRepository;
import com.tienda.electronicos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;
    private final CategoriaProductoRepository categoriaRepository;
    private final AssetRepository assetRepository;

    @Value("${frontend.assets.path:frontend/assets}")
    private String assetsPath;

    // =========================
    // LISTAR (ADMIN / USER)
    // =========================
    @Transactional(readOnly = true)
    public List<ProductoAdminResponse> listarProductos() {
        return productoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================
    // OBTENER POR ID
    // =========================
    @Transactional(readOnly = true)
    public ProductoAdminResponse obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no existe")
                );
        return toResponse(producto);
    }

    // =========================
    // CREAR (ADMIN)
    // =========================
    public ProductoAdminResponse crearProducto(ProductoRequest request) {
        CategoriaProducto categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no existe")
                );

        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoria(categoria);
        producto.setImagenUrl(request.getImagenUrl());

        productoRepository.save(producto);
        return toResponse(producto);
    }

    // =========================
    // CREAR CON IMAGEN (ADMIN)
    // =========================
    public ProductoAdminResponse crearProductoConImagen(ProductoCreateRequest request) {
        // Validar categoría
        CategoriaProducto categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoría no encontrada"));

        // Guardar imagen si viene
        String imagenUrl = null;
        if (request.getImagen() != null && !request.getImagen().isEmpty()) {
            try {
                Asset asset = Asset.builder()
                        .filename(request.getImagen().getOriginalFilename())
                        .content(request.getImagen().getBytes())
                        .contentType(request.getImagen().getContentType())
                        .size(request.getImagen().getSize())
                        .createdAt(OffsetDateTime.now())
                        .build();
                asset = assetRepository.save(asset);
                imagenUrl = "/api/assets/" + asset.getId();
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la imagen", e);
            }
        }

        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .categoria(categoria)
                .imagenUrl(imagenUrl)
                .build();
        producto = productoRepository.save(producto);
        return toResponse(producto);
    }

    // =========================
    // ACTUALIZAR (ADMIN)
    // =========================
    public ProductoAdminResponse actualizarProducto(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no existe")
                );

        CategoriaProducto categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no existe")
                );

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoria(categoria);
        producto.setImagenUrl(request.getImagenUrl());

        productoRepository.save(producto);
        return toResponse(producto);
    }

    // =========================
    // SUBIR IMAGEN (ADMIN) - guardar en disco y en tabla ASSET
    // =========================
    public ProductoAdminResponse uploadImagen(Long id, MultipartFile file) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no existe"));

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Archivo vacío");
        }

        try {
            Path productsDir = Paths.get(assetsPath).toAbsolutePath().resolve("products");
            Files.createDirectories(productsDir);

            String original = Path.of(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename()).getFileName().toString();
            String filename = Instant.now().toEpochMilli() + "_" + original;
            Path target = productsDir.resolve(filename);
            byte[] bytes = file.getBytes();
            Files.write(target, bytes);

            // Guardar en tabla ASSET (si la tabla existe)
            Asset asset = Asset.builder()
                    .filename(filename)
                    .path("products/" + filename)
                    .content(bytes)
                    .contentType(file.getContentType())
                    .size((long) bytes.length)
                    .createdAt(OffsetDateTime.now())
                    .build();
            Asset saved = assetRepository.save(asset);
            log.info("Asset guardado en BD id={}, filename={}, size={}", saved.getId(), saved.getFilename(), saved.getSize());

            // La URL pública que almacenaremos, según tu ejemplo
            String publicPath = "/assets/products/" + filename;
            producto.setImagenUrl(publicPath);

            productoRepository.save(producto);
            log.info("Producto {} actualizado con imagenUrl={}", producto.getIdProducto(), publicPath);
            return toResponse(producto);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo guardar el archivo: " + e.getMessage());
        }
    }

    // =========================
    // ELIMINAR (ADMIN)
    // =========================
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no existe");
        }
        productoRepository.deleteById(id);
    }

    // =========================
    // MAPPER PRIVADO
    // =========================
    private ProductoAdminResponse toResponse(Producto p) {
        Long categoriaId = p.getCategoria() != null ? p.getCategoria().getIdCategoria() : null;
        String categoriaNombre = p.getCategoria() != null ? p.getCategoria().getNombre() : "Sin categoría";
        return new ProductoAdminResponse(
                p.getIdProducto(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                p.getStock(),
                categoriaId,
                categoriaNombre,
                p.getImagenUrl()
        );
    }
}
