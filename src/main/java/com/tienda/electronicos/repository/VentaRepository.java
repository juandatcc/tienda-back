package com.tienda.electronicos.repository;

import com.tienda.electronicos.entity.Venta;
import com.tienda.electronicos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByUsuario(Usuario usuario);

    @Query("SELECT COALESCE(SUM(v.total),0) FROM Venta v")
    BigDecimal sumTotalVentas();

    @Query("SELECT FUNCTION('TO_CHAR', v.fecha, 'YYYY-MM') as mes, COUNT(v), COALESCE(SUM(v.total),0) FROM Venta v GROUP BY FUNCTION('TO_CHAR', v.fecha, 'YYYY-MM') ORDER BY mes")
    List<Object[]> ventasPorMes();
}
