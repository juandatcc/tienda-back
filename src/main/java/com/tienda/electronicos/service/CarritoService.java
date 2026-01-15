package com.tienda.electronicos.service;

import com.tienda.electronicos.entity.Carrito;
import com.tienda.electronicos.entity.CarritoItem;
import com.tienda.electronicos.entity.Producto;
import com.tienda.electronicos.entity.Usuario;
import com.tienda.electronicos.repository.CarritoRepository;
import com.tienda.electronicos.repository.ProductoRepository;
import com.tienda.electronicos.repository.UsuarioRepository;
import com.tienda.electronicos.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional

// @RequiredArgsConstructor
public class CarritoService {

    // =====================================================
    // DEPENDENCIAS
    // =====================================================
    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SecurityUtils securityUtils;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    public CarritoService(
            CarritoRepository carritoRepository,
            ProductoRepository productoRepository,
            UsuarioRepository usuarioRepository,
            SecurityUtils securityUtils
    )
    // Constructor manual en lugar de @RequiredArgsConstructor
    {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.securityUtils = securityUtils;
    }

    // =====================================================
    // AGREGAR PRODUCTO AL CARRITO
    // =====================================================
    public Carrito agregarProducto(Long productoId, int cantidad) {

        // VALIDAR CANTIDAD
        if (cantidad <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a cero");
        }

        // OBTENER USUARIO
        String correoUsuario = securityUtils.obtenerCorreoDesdeContexto();

        // BUSCAR USUARIO Y PRODUCTO
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // BUSCAR PRODUCTO
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // OBTENER O CREAR CARRITO
        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> crearCarrito(usuario));

        // VERIFICAR SI EL PRODUCTO YA ESTÁ EN EL CARRITO
        Optional<CarritoItem> itemExistente = carrito.getItems()
                // Buscar el item correspondiente al productoId
                .stream()
                .filter(item ->
                        item.getProducto().getIdProducto().equals(productoId)
                )
                .findFirst();

        // ACTUALIZAR CANTIDAD O AGREGAR NUEVO ITEM
        if (itemExistente.isPresent()) {
            CarritoItem item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            // Actualizar la cantidad sumando la nueva cantidad
        } else {
            CarritoItem nuevoItem = new CarritoItem(carrito, producto, cantidad);
            carrito.getItems().add(nuevoItem);
        }

        // GUARDAR CAMBIOS
        return carritoRepository.save(carrito);
    }

    // =====================================================
    // ACTUALIZAR CANTIDAD DE PRODUCTO
    // =====================================================

    // ACTUALIZAR CANTIDAD DE PRODUCTO
    public Carrito actualizarCantidad(Long productoId, int nuevaCantidad) {

        // VALIDAR CANTIDAD
        String correoUsuario = securityUtils.obtenerCorreoDesdeContexto();

        // OBTENER USUARIO
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // OBTENER CARRITO
        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        // BUSCAR ITEM EN EL CARRITO
        CarritoItem item = carrito.getItems()

                // Buscar el item correspondiente al productoId
                .stream()
                .filter(i -> i.getProducto().getIdProducto().equals(productoId))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Producto no existe en el carrito")
                );

        // ACTUALIZAR O ELIMINAR ITEM SEGÚN LA NUEVA CANTIDAD
        if (nuevaCantidad <= 0) {
            carrito.getItems().remove(item);
        } else {
            item.setCantidad(nuevaCantidad);
        }

        // GUARDAR CAMBIOS
        return carritoRepository.save(carrito);
    }

    // =====================================================
    // ELIMINAR PRODUCTO DEL CARRITO
    // =====================================================
    public Carrito eliminarProducto(Long productoId) {

        // OBTENER USUARIO
        String correoUsuario = securityUtils.obtenerCorreoDesdeContexto();

        // BUSCAR USUARIO Y CARRITO
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // OBTENER CARRITO
        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        // ELIMINAR ITEM DEL CARRITO
        carrito.getItems().removeIf(item ->
                item.getProducto().getIdProducto().equals(productoId)
        );

        // GUARDAR CAMBIOS
        return carritoRepository.save(carrito);
    }

    // =====================================================
    // OBTENER CARRITO DEL USUARIO
    // =====================================================
    public Carrito obtenerCarrito() {

        // OBTENER USUARIO
        String correoUsuario = securityUtils.obtenerCorreoDesdeContexto();

        // BUSCAR USUARIO Y CARRITO
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // OBTENER CARRITO
        return carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
    }

    // =====================================================
    // MÉTODO PRIVADO
    // =====================================================
    private Carrito crearCarrito(Usuario usuario)

    // CREAR NUEVO CARRITO
    {
        // NUEVO CARRITO
        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        // GUARDAR CARRITO
        return carritoRepository.save(carrito);
    }
}
