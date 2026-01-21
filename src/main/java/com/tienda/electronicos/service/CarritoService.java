package com.tienda.electronicos.service;

import com.tienda.electronicos.entity.Carrito;
import com.tienda.electronicos.entity.CarritoItem;
import com.tienda.electronicos.entity.Producto;
import com.tienda.electronicos.entity.Usuario;
import com.tienda.electronicos.repository.CarritoRepository;
import com.tienda.electronicos.repository.ProductoRepository;
import com.tienda.electronicos.repository.UsuarioRepository;
import com.tienda.electronicos.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SecurityUtils securityUtils;

    public CarritoService(
            CarritoRepository carritoRepository,
            ProductoRepository productoRepository,
            UsuarioRepository usuarioRepository,
            SecurityUtils securityUtils
    ) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.securityUtils = securityUtils;
    }

    public Carrito agregarProducto(Long productoId, int cantidad) {

        if (cantidad <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a cero");
        }

        String correoUsuario = securityUtils.obtenerCorreoDesdeContexto();
        if (correoUsuario == null || correoUsuario.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> crearCarrito(usuario));

        // Defensive init in case items == null
        if (carrito.getItems() == null) {
            carrito.setItems(new ArrayList<>());
        }

        Optional<CarritoItem> itemExistente = carrito.getItems()
                .stream()
                .filter(item -> item.getProducto() != null && item.getProducto().getIdProducto().equals(productoId))
                .findFirst();

        if (itemExistente.isPresent()) {
            CarritoItem item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
        } else {
            CarritoItem nuevoItem = new CarritoItem(carrito, producto, cantidad);
            carrito.getItems().add(nuevoItem);
        }

        return carritoRepository.save(carrito);
    }

    public Carrito actualizarCantidad(Long productoId, int nuevaCantidad) {

        String correoUsuario = securityUtils.obtenerCorreoDesdeContexto();
        if (correoUsuario == null || correoUsuario.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrito no encontrado"));

        if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito no contiene items");
        }

        CarritoItem item = carrito.getItems()
                .stream()
                .filter(i -> i.getProducto() != null && i.getProducto().getIdProducto().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no existe en el carrito"));

        if (nuevaCantidad <= 0) {
            carrito.getItems().remove(item);
        } else {
            item.setCantidad(nuevaCantidad);
        }

        return carritoRepository.save(carrito);
    }

    public Carrito eliminarProducto(Long productoId) {

        String correoUsuario = securityUtils.obtenerCorreoDesdeContexto();
        if (correoUsuario == null || correoUsuario.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrito no encontrado"));

        if (carrito.getItems() != null) {
            carrito.getItems().removeIf(item -> item.getProducto() != null && item.getProducto().getIdProducto().equals(productoId));
        }

        return carritoRepository.save(carrito);
    }

    public Carrito obtenerCarrito() {

        String correoUsuario = securityUtils.obtenerCorreoDesdeContexto();
        if (correoUsuario == null || correoUsuario.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrito no encontrado"));
    }

    private Carrito crearCarrito(Usuario usuario) {
        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        // Inicializar lista de items para evitar NullPointer al añadir items
        carrito.setItems(new ArrayList<>());
        return carritoRepository.save(carrito);
    }
}