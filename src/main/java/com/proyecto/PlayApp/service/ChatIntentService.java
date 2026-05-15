package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.dto.BusquedaDTO;
import com.proyecto.PlayApp.entity.CarritoItem;
import com.proyecto.PlayApp.entity.Pedido;
import com.proyecto.PlayApp.entity.Producto;
import com.proyecto.PlayApp.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ChatIntentService {
    private static final String DEFAULT_PRODUCT_SORT = "[{\"campo\":\"nombre\",\"direccion\":\"asc\"}]";
    private static final String DEFAULT_PEDIDO_SORT = "[{\"campo\":\"timestamp\",\"direccion\":\"desc\"}]";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final CarritoService carritoService;
    private final UsuarioService usuarioService;

    public IntentResolution resolve(String userMessage, String userId) {
        String normalized = normalize(userMessage);

        if (isPedidoIntent(normalized)) {
            return IntentResolution.handled("pedidos", buildPedidosResponse(userId));
        }
        if (isPrecioIntent(normalized)) {
            return IntentResolution.handled("precios", buildPreciosResponse(userMessage));
        }
        if (isRecommendIntent(normalized)) {
            return IntentResolution.handled("recomendar", buildRecommendationResponse());
        }
        if (isFaqIntent(normalized)) {
            return IntentResolution.handled("faq", buildFaqResponse(normalized));
        }

        return IntentResolution.fallback("fallback");
    }

    private String buildFaqResponse(String normalizedMessage) {
        if (containsAny(normalizedMessage, "metodo de pago", "pago", "pagos")) {
            return "Metodos de pago: efectivo y pago en linea. Si prefieres, te ayudo con el flujo de pago de tu pedido.";
        }
        if (containsAny(normalizedMessage, "horario", "hora", "abren", "cierran")) {
            return "Nuestro horario depende de cada vendedor en playa. Te recomiendo validar disponibilidad al momento de pedir.";
        }
        if (containsAny(normalizedMessage, "donde", "ubicacion", "cobertura", "playa")) {
            return "PlayApp conecta productos y servicios en playas de Cartagena. Si quieres, te sugiero opciones disponibles ahora.";
        }
        return "Puedo ayudarte con preguntas frecuentes, recomendaciones, precios y estado de pedidos. Dime que necesitas.";
    }

    private String buildRecommendationResponse() {
        List<Producto> products = findProducts(null, 5);
        if (products.isEmpty()) {
            return "Ahora mismo no hay productos disponibles para recomendar.";
        }

        StringBuilder response = new StringBuilder("Te recomiendo estas opciones:\n");
        for (Producto product : products) {
            response.append("- ")
                    .append(product.getNombre())
                    .append(" | ")
                    .append(formatPrice(product.getPrecio()))
                    .append("\n");
        }
        return response.toString().trim();
    }

    private String buildPreciosResponse(String originalMessage) {
        String term = extractSearchTerm(originalMessage);
        List<Producto> products = findProducts(term, 5);
        if (products.isEmpty()) {
            return "No hay informacion de precios disponible para esa consulta en este momento.";
        }

        StringBuilder response = new StringBuilder();
        if (term != null && !term.isBlank()) {
            response.append("Precios encontrados para \"").append(term).append("\":\n");
        } else {
            response.append("Estos son algunos precios actuales:\n");
        }

        for (Producto product : products) {
            response.append("- ")
                    .append(product.getNombre())
                    .append(": ")
                    .append(formatPrice(product.getPrecio()))
                    .append("\n");
        }
        return response.toString().trim();
    }

    private String buildPedidosResponse(String userId) {
        if (userId == null || userId.isBlank()) {
            return "Para ayudarte con pedidos, comparte tu correo de usuario registrado en PlayApp.";
        }

        Usuario usuario = usuarioService.buscarUsuario(userId.trim());
        if (usuario == null) {
            return "No encontre un usuario asociado a ese correo. Verifica el correo registrado en PlayApp.";
        }

        BusquedaDTO search = BusquedaDTO.builder()
                .id(usuario.getId())
                .page(0)
                .size(3)
                .sort(DEFAULT_PEDIDO_SORT)
                .build();

        List<Pedido> pedidos = pedidoService.buscarPedidoConPaginaOrdenFiltro(search).getContent();
        int carritoItems = countCartItems(usuario.getCorreo());
        if (pedidos.isEmpty()) {
            return "No tienes pedidos registrados por ahora. En carrito tienes " + carritoItems + " item(s).";
        }

        StringBuilder response = new StringBuilder("Estado de tus pedidos recientes:\n");
        pedidos.stream()
                .sorted(Comparator.comparing(Pedido::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(3)
                .forEach(pedido -> response
                        .append("- Pedido ")
                        .append(shortPedidoId(pedido.getId()))
                        .append(": ")
                        .append(pedido.obtenerNombreEstado())
                        .append(", total ")
                        .append(formatPrice(pedido.getTotal()))
                        .append(", fecha ")
                        .append(pedido.getTimestamp() == null ? "N/A" : pedido.getTimestamp().format(DATE_FORMATTER))
                        .append("\n"));

        response.append("Items en carrito: ").append(carritoItems).append(".");
        return response.toString().trim();
    }

    private int countCartItems(String userMail) {
        try {
            List<CarritoItem> items = carritoService.listarCarrito(userMail);
            return items == null ? 0 : items.size();
        } catch (Exception ex) {
            return 0;
        }
    }

    private List<Producto> findProducts(String term, int size) {
        BusquedaDTO search = BusquedaDTO.builder()
                .nombre(term)
                .page(0)
                .size(size)
                .sort(DEFAULT_PRODUCT_SORT)
                .build();

        List<Producto> products = new ArrayList<>(productoService.buscarProductoConPaginaOrdenFiltro(search).getContent());
        if (products.isEmpty() && term != null && !term.isBlank()) {
            search.setNombre(null);
            products = new ArrayList<>(productoService.buscarProductoConPaginaOrdenFiltro(search).getContent());
        }
        return products.stream().limit(size).toList();
    }

    private String extractSearchTerm(String message) {
        String normalized = normalize(message);
        if (!normalized.contains(" de ")) {
            return null;
        }
        String raw = message.substring(normalized.indexOf(" de ") + 4).trim();
        raw = raw.replaceAll("[?!.;,]", " ").trim();
        return raw.isBlank() ? null : raw;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        String noAccents = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return noAccents.toLowerCase(Locale.ROOT).trim();
    }

    private boolean isFaqIntent(String normalizedMessage) {
        return containsAny(normalizedMessage,
                "horario", "hora", "metodo de pago", "pagos", "donde", "ubicacion", "cobertura");
    }

    private boolean isRecommendIntent(String normalizedMessage) {
        return containsAny(normalizedMessage, "recom", "suger", "opcion", "opciones");
    }

    private boolean isPrecioIntent(String normalizedMessage) {
        return containsAny(normalizedMessage, "precio", "precios", "cuanto cuesta", "cuanto vale", "valor");
    }

    private boolean isPedidoIntent(String normalizedMessage) {
        return containsAny(normalizedMessage, "pedido", "pedidos", "estado", "orden", "compra", "carrito");
    }

    private boolean containsAny(String message, String... values) {
        for (String item : values) {
            if (message.contains(item)) {
                return true;
            }
        }
        return false;
    }

    private String formatPrice(Double value) {
        if (value == null) {
            return "N/A";
        }
        return "$" + String.format(Locale.US, "%,.0f", value);
    }

    private String shortPedidoId(String id) {
        if (id == null || id.isBlank()) {
            return "N/A";
        }
        return id.length() <= 8 ? id : id.substring(0, 8);
    }

    public record IntentResolution(String intent, boolean handled, String response) {
        public static IntentResolution handled(String intent, String response) {
            return new IntentResolution(intent, true, response);
        }

        public static IntentResolution fallback(String intent) {
            return new IntentResolution(intent, false, null);
        }
    }
}
