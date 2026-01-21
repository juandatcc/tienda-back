package com.tienda.electronicos.service;

import com.tienda.electronicos.dto.dashboard.DashboardResponse;
import com.tienda.electronicos.repository.ProductoRepository;
import com.tienda.electronicos.repository.UsuarioRepository;
import com.tienda.electronicos.repository.VentaRepository;
import com.tienda.electronicos.repository.DetalleVentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    public DashboardResponse getDashboardSummary() {
        long totalUsuarios = usuarioRepository.count();
        long totalProductos = productoRepository.count();
        long totalVentas = ventaRepository.count();
        BigDecimal ingresosTotales = ventaRepository.sumTotalVentas();

        // Producto más vendido
        DashboardResponse.ProductoResumen productoMasVendido = null;
        List<Object[]> topProductos = detalleVentaRepository.productoMasVendido();
        if (!topProductos.isEmpty()) {
            Object[] row = topProductos.getFirst();
            productoMasVendido = DashboardResponse.ProductoResumen.builder()
                    .id((Long) row[0])
                    .nombre((String) row[1])
                    .cantidadVendida((Long) row[2])
                    .build();
        }

        // Ventas por mes
        List<Object[]> ventasPorMesRaw = ventaRepository.ventasPorMes();
        List<DashboardResponse.VentasPorMes> ventasPorMes = new ArrayList<>();
        for (Object[] row : ventasPorMesRaw) {
            ventasPorMes.add(DashboardResponse.VentasPorMes.builder()
                    .mes((String) row[0])
                    .cantidad(((Number) row[1]).longValue())
                    .total((BigDecimal) row[2])
                    .build());
        }

        return DashboardResponse.builder()
                .totalUsuarios(totalUsuarios)
                .totalProductos(totalProductos)
                .totalVentas(totalVentas)
                .ingresosTotales(ingresosTotales)
                .productoMasVendido(productoMasVendido)
                .ventasPorMes(ventasPorMes)
                .build();
    }
}
