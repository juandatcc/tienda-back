package com.tienda.electronicos.service;

/**
 * Puerto de salida: dónde se guardan las imágenes subidas.
 * Hoy lo implementa {@link SupabaseAlmacenImagenes}; otro destino se agrega
 * sin tocar {@link ImagenService}.
 */
public interface AlmacenImagenes {

    /**
     * Guarda el archivo y devuelve la URL pública con la que se muestra.
     *
     * @param ruta          ruta relativa dentro del almacén (p. ej. {@code abc.png})
     * @param tipoContenido MIME ya validado ({@code image/png}, ...)
     */
    String guardar(String ruta, String tipoContenido, byte[] contenido);
}
