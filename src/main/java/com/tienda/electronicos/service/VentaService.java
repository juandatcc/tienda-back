package com.tienda.electronicos.service;

import com.tienda.electronicos.dto.venta.VentaResponse;
import com.tienda.electronicos.entity.*;
import com.tienda.electronicos.repository.*;
import com.tienda.electronicos.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final SecurityUtils securityUtils;


    // =====================================================
    // REALIZAR VENTA
    // =====================================================
    @Transactional
    public VentaResponse realizarVenta() {

        // Usuario autenticado (desde el contexto de seguridad)
        String correo = securityUtils.obtenerCorreoDesdeContexto();

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no tiene carrito"));

        if (carrito.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito está vacío");
        }

        // Cliente asociado al usuario (se crea si aún no existe)
        Cliente cliente = clienteRepository.findByUsuario(usuario)
                .orElseGet(() -> clienteRepository.save(Cliente.builder()
                        .nombre(usuario.getNombre() != null && !usuario.getNombre().isBlank()
                                ? usuario.getNombre()
                                : usuario.getCorreo())
                        .direccion(usuario.getDireccion())
                        .telefono(usuario.getTelefono())
                        .usuario(usuario)
                        .build()));

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setCliente(cliente);
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(BigDecimal.ZERO);

        BigDecimal total = BigDecimal.ZERO;

        for (CarritoItem item : carrito.getItems()) {

            Producto producto = productoRepository.findById(
                    item.getProducto().getIdProducto()
            ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

            // VALIDAR STOCK
            if (producto.getStock() == null || producto.getStock() < item.getCantidad()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Stock insuficiente para el producto: " + producto.getNombre()
                );
            }

            // DESCONTAR STOCK
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setSubtotal(item.getSubtotal());

            total = total.add(item.getSubtotal());
            venta.getDetalles().add(detalle);
        }

        venta.setTotal(total);

        // GUARDAR VENTA (los detalles se guardan en cascada)
        Venta ventaGuardada = ventaRepository.save(venta);

        // LIMPIAR CARRITO
        carrito.getItems().clear();
        carritoRepository.save(carrito);

        return toResponse(ventaGuardada);
    }

    // =====================================================
    // MAPPER PRIVADO
    // =====================================================
    private VentaResponse toResponse(Venta venta) {
        return new VentaResponse(
                venta.getIdVenta(),
                venta.getFecha(),
                venta.getTotal(),
                venta.getDetalles().stream()
                        .map(d -> new VentaResponse.DetalleVentaResponse(
                                d.getProducto().getIdProducto(),
                                d.getProducto().getNombre(),
                                d.getCantidad(),
                                d.getPrecioUnitario(),
                                d.getSubtotal()
                        ))
                        .toList()
        );
    }
}
