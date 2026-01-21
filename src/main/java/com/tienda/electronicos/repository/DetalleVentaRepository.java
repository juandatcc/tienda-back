package com.tienda.electronicos.repository;

import com.tienda.electronicos.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    @Query("SELECT d.producto.idProducto, d.producto.nombre, SUM(d.cantidad) as total FROM DetalleVenta d GROUP BY d.producto.idProducto, d.producto.nombre ORDER BY total DESC")
    List<Object[]> productoMasVendido();
}
