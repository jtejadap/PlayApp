package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.dto.ReporteDTOs.IngresoMensualDTO;
import com.proyecto.PlayApp.dto.ReporteDTOs.ProductoTopDTO;
import com.proyecto.PlayApp.dto.ReporteDTOs.ReportePedidosDTO;
import com.proyecto.PlayApp.dto.ReporteDTOs.ReporteProductosDTO;
import com.proyecto.PlayApp.dto.ReporteDTOs.ReporteRestaurantesDTO;
import com.proyecto.PlayApp.dto.ReporteDTOs.RestauranteTopDTO;
import com.proyecto.PlayApp.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reportes")
public class ReporteController {
    private final ReporteService reportes;

    @GetMapping("/pedidos")
    public ResponseEntity<ReportePedidosDTO> pedidos(Principal principal) {
        return noCache(reportes.reportePedidos(principal.getName()));
    }

    @GetMapping("/productos")
    public ResponseEntity<ReporteProductosDTO> productos(Principal principal) {
        return noCache(reportes.reporteProductos(principal.getName()));
    }

    @GetMapping("/restaurantes")
    public ResponseEntity<ReporteRestaurantesDTO> restaurantes(Principal principal) {
        return noCache(reportes.reporteRestaurantes(principal.getName()));
    }

    @GetMapping("/ingresos-mensuales")
    public ResponseEntity<List<IngresoMensualDTO>> ingresosMensuales(Principal principal) {
        return noCache(reportes.ingresosMensuales(principal.getName()));
    }

    @GetMapping("/productos-top")
    public ResponseEntity<List<ProductoTopDTO>> productosTop(
            @RequestParam(name = "limite", defaultValue = "10") int limite,
            Principal principal
    ) {
        return noCache(reportes.productosTop(normalizarLimite(limite), principal.getName()));
    }

    @GetMapping("/restaurantes-top")
    public ResponseEntity<List<RestauranteTopDTO>> restaurantesTop(
            @RequestParam(name = "limite", defaultValue = "10") int limite,
            Principal principal
    ) {
        return noCache(reportes.restaurantesTop(normalizarLimite(limite), principal.getName()));
    }

    private <T> ResponseEntity<T> noCache(T body) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(body);
    }

    private int normalizarLimite(int limite) {
        return Math.max(1, Math.min(limite, 100));
    }
}
