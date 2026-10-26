package com.tienda.electronicos.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImagenServiceTest {

    /** Almacén falso: registra lo que se le pide guardar, sin red. */
    static class AlmacenFalso implements AlmacenImagenes {
        final List<String[]> guardados = new ArrayList<>();

        @Override
        public String guardar(String ruta, String tipoContenido, byte[] contenido) {
            guardados.add(new String[]{ruta, tipoContenido});
            return "https://almacen.test/" + ruta;
        }
    }

    private final AlmacenFalso almacen = new AlmacenFalso();
    private final ImagenService servicio = new ImagenService(almacen);

    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0};
    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0};
    private static final byte[] WEBP = {'R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'E', 'B', 'P'};
    private static final byte[] GIF = {'G', 'I', 'F', '8', '9', 'a', 0};

    private MockMultipartFile archivo(String nombre, String mime, byte[] bytes) {
        return new MockMultipartFile("archivo", nombre, mime, bytes);
    }

    private int estado(Runnable accion) {
        return assertThrows(ResponseStatusException.class, accion::run).getStatusCode().value();
    }

    @Test
    void aceptaCadaFormatoPorSusBytesYGuardaConExtensionCorrecta() {
        assertTrue(servicio.subirImagenProducto(archivo("a.png", "image/png", JPEG)).endsWith(".jpg"));
        assertTrue(servicio.subirImagenProducto(archivo("a.jpg", "image/jpeg", PNG)).endsWith(".png"));
        assertTrue(servicio.subirImagenProducto(archivo("a.png", "image/png", WEBP)).endsWith(".webp"));
        assertTrue(servicio.subirImagenProducto(archivo("a.png", "image/png", GIF)).endsWith(".gif"));
        assertEquals("image/jpeg", almacen.guardados.get(0)[1]);
    }

    @Test
    void ignoraElContentTypeYElNombreQueDeclaraElCliente() {
        // Un ejecutable disfrazado de imagen: el Content-Type dice PNG, los bytes no
        byte[] exe = {'M', 'Z', (byte) 0x90, 0, 3, 0};
        assertEquals(400, estado(() -> servicio.subirImagenProducto(archivo("x.png", "image/png", exe))));
        assertTrue(almacen.guardados.isEmpty());
    }

    @Test
    void nuncaUsaElNombreOriginalComoRuta() {
        servicio.subirImagenProducto(archivo("../../etc/passwd.png", "image/png", PNG));
        String ruta = almacen.guardados.get(0)[0];
        assertFalse(ruta.contains("/") || ruta.contains(".."));
        assertTrue(ruta.matches("[0-9a-f-]{36}[.]png"));
    }

    @Test
    void rechazaArchivoVacioOAusente() {
        assertEquals(400, estado(() -> servicio.subirImagenProducto(archivo("v.png", "image/png", new byte[0]))));
        assertEquals(400, estado(() -> servicio.subirImagenProducto(null)));
    }

    @Test
    void rechazaMasDeCincoMegas() {
        byte[] grande = new byte[(int) ImagenService.MAX_BYTES + 1];
        System.arraycopy(PNG, 0, grande, 0, PNG.length);
        assertEquals(400, estado(() -> servicio.subirImagenProducto(archivo("g.png", "image/png", grande))));
        assertTrue(almacen.guardados.isEmpty());
    }

    @Test
    void aceptaExactamenteCincoMegas() {
        byte[] justo = new byte[(int) ImagenService.MAX_BYTES];
        System.arraycopy(PNG, 0, justo, 0, PNG.length);
        assertTrue(servicio.subirImagenProducto(archivo("j.png", "image/png", justo)).endsWith(".png"));
    }
}
