package com.tienda.electronicos.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardResponse {
    private long totalUsuarios;
    private long totalProductos;
    private long totalVentas;
    private BigDecimal ingresosTotales;
    private ProductoResumen productoMasVendido;
    private List<VentasPorMes> ventasPorMes;

    @Data
    @Builder
    public static class ProductoResumen {
        private Long id;
        private String nombre;
        private long cantidadVendida;
    }

    @Data
    @Builder
    public static class VentasPorMes {
        private String mes;
        private long cantidad;
        private BigDecimal total;
    }
}

