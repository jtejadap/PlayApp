package com.proyecto.PlayApp.dto;

import java.util.List;

public final class ReporteDTOs {
    private ReporteDTOs() {
    }

    public record ConteoValorDTO(
            String clave,
            String nombre,
            long cantidad,
            double valor
    ) {
    }

    public record IngresoMensualDTO(
            int anio,
            int mes,
            String periodo,
            long pedidos,
            double ingresos
    ) {
    }

    public record ProductoTopDTO(
            String productoId,
            String nombre,
            Integer categoria,
            String categoriaNombre,
            long cantidadVendida,
            double ingresos,
            long pedidos
    ) {
    }

    public record ProductoStockDTO(
            String productoId,
            String nombre,
            Integer categoria,
            String categoriaNombre,
            double stock,
            double precio,
            double valorInventario
    ) {
    }

    public record ProductoCategoriaDTO(
            Integer categoria,
            String categoriaNombre,
            long productos,
            double stock,
            double valorInventario
    ) {
    }

    public record RestauranteTopDTO(
            String restauranteId,
            String nombre,
            long pedidos,
            long productosVendidos,
            double ingresos
    ) {
    }

    public record ReportePedidosDTO(
            long totalPedidos,
            double ingresosTotales,
            double promedioProductosPorPedido,
            List<IngresoMensualDTO> ingresosMensuales,
            List<ConteoValorDTO> pedidosPorEstado,
            List<ConteoValorDTO> pedidosPorMetodoPago,
            List<ProductoTopDTO> productosMasVendidos
    ) {
    }

    public record ReporteProductosDTO(
            long totalProductos,
            double valorTotalInventario,
            List<ProductoStockDTO> productosConMasStock,
            List<ProductoTopDTO> productosMasVendidos,
            List<ProductoCategoriaDTO> productosPorCategoria,
            List<ProductoTopDTO> ventasPorProducto
    ) {
    }

    public record ReporteRestaurantesDTO(
            long totalRestaurantes,
            double calificacionPromedio,
            List<RestauranteTopDTO> restaurantesConMasPedidos,
            List<RestauranteTopDTO> restaurantesConMayoresIngresos,
            List<RestauranteTopDTO> productosVendidosPorRestaurante
    ) {
    }
}
