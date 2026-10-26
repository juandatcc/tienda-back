package com.tienda.electronicos.controller.imagen;

import com.tienda.electronicos.dto.imagen.ImagenSubidaResponse;
import com.tienda.electronicos.service.ImagenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Subida de imágenes de producto (solo ADMIN). No toca Oracle: guarda el archivo
 * en Supabase Storage y devuelve su URL pública para que el producto la guarde.
 */
@RestController
@RequestMapping("/api/admin/imagenes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ImagenAdminController {

    private final ImagenService imagenService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ImagenSubidaResponse subir(@RequestParam("archivo") MultipartFile archivo) {
        return new ImagenSubidaResponse(imagenService.subirImagenProducto(archivo));
    }
}
