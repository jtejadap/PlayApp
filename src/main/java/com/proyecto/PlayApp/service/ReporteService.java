package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.dto.ReporteDTOs.ConteoValorDTO;
import com.proyecto.PlayApp.dto.ReporteDTOs.IngresoMensualDTO;
import com.proyecto.PlayApp.dto.ReporteDTOs.ProductoCategoriaDTO;
import com.proyecto.PlayApp.dto.ReporteDTOs.ProductoStockDTO;
import com.proyecto.PlayApp.dto.ReporteDTOs.ProductoTopDTO;
import com.proyecto.PlayApp.dto.ReporteDTOs.ReportePedidosDTO;
import com.proyecto.PlayApp.dto.ReporteDTOs.ReporteProductosDTO;
import com.proyecto.PlayApp.dto.ReporteDTOs.ReporteRestaurantesDTO;
import com.proyecto.PlayApp.dto.ReporteDTOs.RestauranteTopDTO;
import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.repository.ReporteRepository;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {
    private static final int LIMITE_TOP = 10;
    public static final String ADMIN_GLOBAL_CORREO = "adminplayapp01@gmail.com";

    private final ReporteRepository reportes;
    private final UsuarioRepository usuarios;

    public ReportePedidosDTO reportePedidos(String correoAdmin) {
        String restauranteId = restauranteIdSiAplica(correoAdmin);
        Document resumen = firstOrEmpty(reportes.resumenPedidos(restauranteId));
        return new ReportePedidosDTO(
                asLong(resumen.get("totalPedidos")),
                asDouble(resumen.get("ingresosTotales")),
                promedioProductosPorPedido(restauranteId),
                ingresosMensuales(correoAdmin),
                pedidosPorEstado(restauranteId),
                pedidosPorMetodoPago(restauranteId),
                productosTop(LIMITE_TOP, correoAdmin)
        );
    }

    public ReporteProductosDTO reporteProductos(String correoAdmin) {
        String restauranteId = restauranteIdSiAplica(correoAdmin);
        return new ReporteProductosDTO(
                reportes.contarProductos(restauranteId),
                valorTotalInventario(restauranteId),
                productosConMasStock(LIMITE_TOP, restauranteId),
                productosTop(LIMITE_TOP, correoAdmin),
                productosPorCategoria(restauranteId),
                productosTop(100, correoAdmin)
        );
    }

    public ReporteRestaurantesDTO reporteRestaurantes(String correoAdmin) {
        String restauranteId = restauranteIdSiAplica(correoAdmin);
        return new ReporteRestaurantesDTO(
                restauranteId == null ? reportes.contarRestaurantes() : 1,
                calificacionPromedio(),
                restaurantesConMasPedidos(LIMITE_TOP, restauranteId),
                restaurantesTop(LIMITE_TOP, correoAdmin),
                productosVendidosPorRestaurante(LIMITE_TOP, restauranteId)
        );
    }

    public List<IngresoMensualDTO> ingresosMensuales(String correoAdmin) {
        return reportes.ingresosMensuales(restauranteIdSiAplica(correoAdmin)).stream()
                .map(document -> {
                    Document id = document.get("_id", Document.class);
                    int anio = asInt(id.get("anio"));
                    int mes = asInt(id.get("mes"));
                    return new IngresoMensualDTO(
                            anio,
                            mes,
                            "%04d-%02d".formatted(anio, mes),
                            asLong(document.get("pedidos")),
                            asDouble(document.get("ingresos"))
                    );
                })
                .toList();
    }

    public List<ProductoTopDTO> productosTop(int limite, String correoAdmin) {
        return reportes.productosMasVendidos(limite, restauranteIdSiAplica(correoAdmin)).stream()
                .map(this::toProductoTop)
                .toList();
    }

    public List<RestauranteTopDTO> restaurantesTop(int limite, String correoAdmin) {
        return reportes.ventasPorRestaurante(limite, restauranteIdSiAplica(correoAdmin)).stream()
                .map(this::toRestauranteTop)
                .toList();
    }

    public boolean esAdminGlobal(String correoAdmin) {
        return ADMIN_GLOBAL_CORREO.equalsIgnoreCase(correoAdmin);
    }

    private List<RestauranteTopDTO> restaurantesConMasPedidos(int limite, String restauranteId) {
        if (restauranteId != null) {
            return reportes.ventasPorRestaurante(1, restauranteId).stream()
                    .map(this::toRestauranteTop)
                    .toList();
        }
        return reportes.restaurantesConMasPedidos(limite).stream()
                .map(this::toRestauranteTop)
                .toList();
    }

    private List<ConteoValorDTO> pedidosPorEstado(String restauranteId) {
        return reportes.pedidosPorEstado(restauranteId).stream()
                .map(document -> {
                    Object clave = document.get("_id");
                    return new ConteoValorDTO(
                            String.valueOf(clave),
                            nombreEstado(clave),
                            asLong(document.get("cantidad")),
                            asDouble(document.get("valor"))
                    );
                })
                .toList();
    }

    private List<ConteoValorDTO> pedidosPorMetodoPago(String restauranteId) {
        return reportes.pedidosPorMetodoPago(restauranteId).stream()
                .map(document -> {
                    String metodo = String.valueOf(document.get("_id"));
                    return new ConteoValorDTO(
                            metodo,
                            "SIN_METODO".equals(metodo) ? "Sin metodo" : metodo,
                            asLong(document.get("cantidad")),
                            asDouble(document.get("valor"))
                    );
                })
                .toList();
    }

    private double promedioProductosPorPedido(String restauranteId) {
        return asDouble(firstOrEmpty(reportes.promedioProductosPorPedido(restauranteId)).get("promedio"));
    }

    private double valorTotalInventario(String restauranteId) {
        return asDouble(firstOrEmpty(reportes.valorTotalInventario(restauranteId)).get("valorTotalInventario"));
    }

    private List<ProductoStockDTO> productosConMasStock(int limite, String restauranteId) {
        return reportes.productosConMasStock(limite, restauranteId).stream()
                .map(document -> {
                    Integer categoria = asNullableInt(document.get("categoria"));
                    return new ProductoStockDTO(
                            stringValue(document.get("_id")),
                            stringValue(document.get("nombre")),
                            categoria,
                            nombreCategoria(categoria),
                            asDouble(document.get("stock")),
                            asDouble(document.get("precio")),
                            asDouble(document.get("valorInventario"))
                    );
                })
                .toList();
    }

    private List<ProductoCategoriaDTO> productosPorCategoria(String restauranteId) {
        return reportes.productosPorCategoria(restauranteId).stream()
                .map(document -> {
                    Integer categoria = asNullableInt(document.get("_id"));
                    return new ProductoCategoriaDTO(
                            categoria,
                            nombreCategoria(categoria),
                            asLong(document.get("productos")),
                            asDouble(document.get("stock")),
                            asDouble(document.get("valorInventario"))
                    );
                })
                .toList();
    }

    private double calificacionPromedio() {
        return asDouble(firstOrEmpty(reportes.calificacionPromedio()).get("promedio"));
    }

    private List<RestauranteTopDTO> productosVendidosPorRestaurante(int limite, String restauranteId) {
        return reportes.ventasPorRestaurante(limite, restauranteId).stream()
                .map(this::toRestauranteTop)
                .toList();
    }

    private String restauranteIdSiAplica(String correoAdmin) {
        if (correoAdmin == null || esAdminGlobal(correoAdmin)) {
            return null;
        }
        Usuario usuario = usuarios.findUsuarioByCorreo(correoAdmin);
        return usuario == null ? null : usuario.getId();
    }

    private ProductoTopDTO toProductoTop(Document document) {
        Document id = document.get("_id", Document.class);
        Integer categoria = asNullableInt(id.get("categoria"));
        return new ProductoTopDTO(
                stringValue(id.get("productoId")),
                stringValue(id.get("nombre")),
                categoria,
                nombreCategoria(categoria),
                asLong(document.get("cantidadVendida")),
                asDouble(document.get("ingresos")),
                asLong(document.get("pedidos"))
        );
    }

    private RestauranteTopDTO toRestauranteTop(Document document) {
        return new RestauranteTopDTO(
                stringValue(document.get("restauranteId")),
                stringValue(document.get("nombre")),
                asLong(document.get("pedidos")),
                asLong(document.get("productosVendidos")),
                asDouble(document.get("ingresos"))
        );
    }

    private Document firstOrEmpty(List<Document> documents) {
        return documents.isEmpty() ? new Document() : documents.getFirst();
    }

    private String nombreEstado(Object estado) {
        if (!(estado instanceof Number number)) {
            return "Sin estado";
        }
        return switch (number.intValue()) {
            case 0 -> "Recibido";
            case 1 -> "En preparacion";
            case 2 -> "Terminado";
            default -> "Sin estado";
        };
    }

    private String nombreCategoria(Integer categoria) {
        if (categoria == null) {
            return "Sin asignar";
        }
        return switch (categoria) {
            case 1 -> "Comida";
            case 2 -> "Bebida";
            case 3 -> "Servicio";
            default -> "Sin asignar";
        };
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private int asInt(Object value) {
        return value instanceof Number number ? number.intValue() : 0;
    }

    private Integer asNullableInt(Object value) {
        return value instanceof Number number ? number.intValue() : null;
    }

    private long asLong(Object value) {
        return value instanceof Number number ? number.longValue() : 0L;
    }

    private double asDouble(Object value) {
        return value instanceof Number number ? number.doubleValue() : 0D;
    }
}
