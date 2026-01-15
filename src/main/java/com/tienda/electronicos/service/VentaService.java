package com.tienda.electronicos.service;

import com.tienda.electronicos.entity.*;
import com.tienda.electronicos.repository.*;
import com.tienda.electronicos.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository ventaDetalleRepository;
    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;


    // =====================================================
    // REALIZAR VENTA
    // =====================================================
    @Transactional
    public Venta realizarVenta() {

        String token = jwtUtil.obtenerTokenActual();
        String correo = jwtUtil.extraerCorreo(token);

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("El usuario no tiene carrito"));

        if (carrito.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(BigDecimal.ZERO);

        List<DetalleVenta> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CarritoItem item : carrito.getItems()) {

            Producto producto = productoRepository.findById(
                    item.getProducto().getIdProducto()
            ).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // VALIDAR STOCK
            if (producto.getStock() < item.getCantidad()) {
                throw new RuntimeException(
                        "Stock insuficiente para el producto: " + producto.getNombre()
                );
            }

            // DESCONTAR STOCK
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setSubtotal(item.getSubtotal());

            total = total.add(item.getSubtotal());
            detalles.add(detalle);
        }

        venta.setTotal(total);
        venta.setDetalle(detalles);

        // GUARDAR VENTA
        Venta ventaGuardada = ventaRepository.save(venta);

        // ASOCIAR DETALLES
        for (DetalleVenta detalle : detalles) {
            detalle.setVenta(ventaGuardada);
        }

        ventaDetalleRepository.saveAll(detalles);

        // LIMPIAR CARRITO
        carrito.getItems().clear();
        carritoRepository.save(carrito);

        return ventaGuardada;
    }


}
