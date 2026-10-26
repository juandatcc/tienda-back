package com.tienda.electronicos.dto.imagen;

/** URL pública de la imagen recién subida; el frontend la envía como imagenUrl al guardar el producto. */
public record ImagenSubidaResponse(String url) {
}
