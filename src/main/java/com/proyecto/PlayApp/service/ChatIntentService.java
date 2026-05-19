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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatIntentService {
    private static final String DEFAULT_PRODUCT_SORT = "[{\"campo\":\"nombre\",\"direccion\":\"asc\"}]";
    private static final String DEFAULT_PEDIDO_SORT = "[{\"campo\":\"timestamp\",\"direccion\":\"desc\"}]";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final int CATEGORY_FOOD = 1;
    private static final int CATEGORY_DRINK = 2;
    private static final int CATEGORY_SERVICE = 3;

    private static final Set<String> STOP_WORDS = Set.of(
            "de", "del", "la", "las", "el", "los", "un", "una", "unos", "unas", "por", "para", "me", "quiero",
            "muestrame", "muestreme", "que", "cual", "cuanto", "vale", "cuesta", "precio", "precios", "valor", "tienen"
    );

    private static final List<String> REFRESHING_KEYWORDS = List.of(
            "refrescante", "refrescantes", "frio", "fria", "frias", "frios", "helado", "helada", "calor",
            "bebida", "bebidas", "jugo", "jugo", "jugos", "refresco", "refrescos", "limonada", "gaseosa", "coca", "cola"
    );

    private static final List<String> SNACK_KEYWORDS = List.of(
            "snack", "snacks", "pasaboca", "pasabocas", "fritura", "frituras", "papas", "nachos", "galleta", "galletas"
    );

    private static final List<String> FOOD_KEYWORDS = List.of(
            "comida", "comidas", "almuerzo", "cena", "desayuno", "hamburguesa", "arepa", "pescado", "marisco", "ceviche"
    );

    private static final List<String> DRINK_KEYWORDS = List.of(
            "bebida", "bebidas", "jugo", "jugos", "refresco", "refrescos", "gaseosa", "agua", "cerveza", "coctel", "coca"
    );

    private static final List<String> SERVICE_KEYWORDS = List.of(
            "servicio", "servicios", "carpa", "sombrilla", "mesa", "moto", "alquiler"
    );

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
            return buildPriceIntentResponse(userMessage, normalized);
        }
        if (isRecommendIntent(normalized) || isProductBrowseIntent(normalized)) {
            return buildProductIntentResponse(userMessage, normalized);
        }
        if (isFaqIntent(normalized)) {
            return IntentResolution.handled("faq", buildFaqResponse(normalized));
        }

        return IntentResolution.fallback("fallback");
    }

    private IntentResolution buildProductIntentResponse(String originalMessage, String normalizedMessage) {
        ProductQuery query = buildProductQuery(originalMessage, normalizedMessage);
        List<Producto> products = findRelevantProducts(query, 6);

        if (products.isEmpty()) {
            return IntentResolution.handled("productos", "No encontre productos disponibles para esa solicitud en este momento.");
        }

        String response = buildProductListResponse(products, query.responseTitle());
        String modelContext = buildProductModelContext(products, query.responseTitle());

        return IntentResolution.handledWithContext("productos", response, modelContext, true);
    }

    private IntentResolution buildPriceIntentResponse(String originalMessage, String normalizedMessage) {
        String productTarget = extractProductTarget(originalMessage, normalizedMessage);
        Integer categoryHint = detectCategoryFromMessage(normalizedMessage);

        ProductQuery query = new ProductQuery(
                productTarget,
                categoryHint,
                buildKeywordHints(normalizedMessage, categoryHint),
                "Consulta de precios",
                true
        );

        List<Producto> products = findRelevantProducts(query, 3);
        if (products.isEmpty()) {
            if (productTarget != null && !productTarget.isBlank()) {
                return IntentResolution.handled("precios", "No encontre precios para \"" + productTarget + "\" en el inventario actual.");
            }
            return IntentResolution.handled("precios", "No hay informacion de precios disponible para esa consulta en este momento.");
        }

        String response = buildPriceResponse(products, productTarget);
        String modelContext = buildPriceModelContext(products, productTarget);
        return IntentResolution.handledWithContext("precios", response, modelContext, true);
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

    private ProductQuery buildProductQuery(String originalMessage, String normalizedMessage) {
        Integer category = detectCategoryFromMessage(normalizedMessage);
        String searchTerm = extractSearchTermFromBrowse(originalMessage, normalizedMessage);
        List<String> keywordHints = buildKeywordHints(normalizedMessage, category);

        String title;
        if (isRefreshingIntent(normalizedMessage)) {
            title = "Opciones refrescantes";
            category = CATEGORY_DRINK;
            keywordHints = mergeKeywordHints(keywordHints, REFRESHING_KEYWORDS);
        } else if (containsAny(normalizedMessage, "bebida", "bebidas", "jugo", "jugos", "refresco", "refrescos", "frio", "fria", "calor")) {
            title = "Bebidas y opciones frias";
        } else if (containsAny(normalizedMessage, "snack", "snacks", "pasaboca", "pasabocas")) {
            title = "Snacks";
        } else if (containsAny(normalizedMessage, "comida", "comidas", "almuerzo", "cena")) {
            title = "Comida";
        } else if (category != null && category == CATEGORY_SERVICE) {
            title = "Servicios";
        } else if (isRecommendIntent(normalizedMessage)) {
            title = "Recomendaciones";
        } else {
            title = "Productos relacionados";
        }

        return new ProductQuery(searchTerm, category, keywordHints, title, true);
    }

    private List<Producto> findRelevantProducts(ProductQuery query, int limit) {
        int fetchSize = Math.max(limit * 4, 24);
        List<Producto> candidates = fetchCandidates(query.category(), query.searchTerm(), fetchSize);

        if (candidates.isEmpty() && query.category() != null) {
            candidates = fetchCandidates(query.category(), null, fetchSize);
        }
        if (candidates.isEmpty() && query.searchTerm() != null && !query.searchTerm().isBlank()) {
            candidates = fetchCandidates(null, query.searchTerm(), fetchSize);
        }

        if (candidates.isEmpty()) {
            return List.of();
        }

        boolean strictFiltering = (query.searchTerm() != null && !query.searchTerm().isBlank())
                || !query.keywordHints().isEmpty()
                || query.category() != null;

        List<ProductScore> ranked = candidates.stream()
                .map(product -> new ProductScore(product, scoreProduct(product, query)))
                .filter(item -> !strictFiltering || item.score() > 0)
                .sorted(Comparator
                        .comparingInt(ProductScore::score).reversed()
                        .thenComparing(item -> normalize(item.product().getNombre())))
                .limit(limit)
                .toList();

        return ranked.stream().map(ProductScore::product).toList();
    }

    private int scoreProduct(Producto product, ProductQuery query) {
        String normalizedName = normalize(product.getNombre());
        String normalizedDescription = normalize(product.getDescripcion());
        int score = 0;

        if (query.category() != null && query.category().equals(product.getCategoria())) {
            score += 35;
        }

        String searchTerm = query.searchTerm();
        if (searchTerm != null && !searchTerm.isBlank()) {
            String normalizedTerm = normalize(searchTerm);
            if (normalizedName.equals(normalizedTerm)) {
                score += 200;
            }
            if (normalizedName.contains(normalizedTerm)) {
                score += 140;
            }
            if (normalizedDescription.contains(normalizedTerm)) {
                score += 90;
            }

            for (String token : splitTokens(normalizedTerm)) {
                if (token.length() < 3) {
                    continue;
                }
                if (normalizedName.contains(token)) {
                    score += 55;
                }
                if (normalizedDescription.contains(token)) {
                    score += 30;
                }
            }
        }

        for (String keyword : query.keywordHints()) {
            String normalizedKeyword = normalize(keyword);
            if (normalizedKeyword.length() < 3) {
                continue;
            }
            if (normalizedName.contains(normalizedKeyword)) {
                score += 30;
            }
            if (normalizedDescription.contains(normalizedKeyword)) {
                score += 18;
            }
        }

        if (product.getStock() != null && product.getStock() > 0) {
            score += 8;
        }

        return score;
    }

    private List<String> buildKeywordHints(String normalizedMessage, Integer categoryHint) {
        LinkedHashSet<String> hints = new LinkedHashSet<>();
        if (categoryHint != null) {
            if (categoryHint == CATEGORY_DRINK) {
                hints.addAll(DRINK_KEYWORDS);
            } else if (categoryHint == CATEGORY_FOOD) {
                hints.addAll(FOOD_KEYWORDS);
            } else if (categoryHint == CATEGORY_SERVICE) {
                hints.addAll(SERVICE_KEYWORDS);
            }
        }

        if (isRefreshingIntent(normalizedMessage)) {
            hints.addAll(REFRESHING_KEYWORDS);
        }
        if (containsAny(normalizedMessage, "snack", "snacks", "pasaboca", "pasabocas")) {
            hints.addAll(SNACK_KEYWORDS);
        }
        if (containsAny(normalizedMessage, "comida", "comidas", "almuerzo", "cena")) {
            hints.addAll(FOOD_KEYWORDS);
        }
        if (containsAny(normalizedMessage, "bebida", "bebidas", "jugo", "jugos", "refresco", "refrescos")) {
            hints.addAll(DRINK_KEYWORDS);
        }

        return new ArrayList<>(hints);
    }

    private List<String> mergeKeywordHints(List<String> base, List<String> extra) {
        LinkedHashSet<String> merged = new LinkedHashSet<>(base);
        merged.addAll(extra);
        return new ArrayList<>(merged);
    }

    private String buildProductListResponse(List<Producto> products, String title) {
        StringBuilder response = new StringBuilder(title).append(":\n");
        for (Producto product : products) {
            response.append("- ")
                    .append(product.getNombre())
                    .append(" | ")
                    .append(formatPrice(product.getPrecio()))
                    .append("\n");
        }
        return response.toString().trim();
    }

    private String buildPriceResponse(List<Producto> products, String target) {
        if (products.size() == 1) {
            Producto product = products.getFirst();
            return "El precio de " + product.getNombre() + " es " + formatPrice(product.getPrecio()) + ".";
        }

        StringBuilder response = new StringBuilder();
        if (target != null && !target.isBlank()) {
            response.append("Precios relacionados con \"").append(target).append("\":\n");
        } else {
            response.append("Precios encontrados:\n");
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

    private String buildProductModelContext(List<Producto> products, String title) {
        String items = products.stream()
                .map(product -> "- " + product.getNombre() + " | " + formatPrice(product.getPrecio()) + " | categoria: " + product.obtenerNombreCategoria())
                .collect(Collectors.joining("\n"));
        return title + "\n" + items;
    }

    private String buildPriceModelContext(List<Producto> products, String target) {
        String header = target == null || target.isBlank()
                ? "Consulta de precios"
                : "Consulta de precio para: " + target;
        String items = products.stream()
                .map(product -> "- " + product.getNombre() + " | precio: " + formatPrice(product.getPrecio()))
                .collect(Collectors.joining("\n"));
        return header + "\n" + items;
    }

    private List<Producto> fetchCandidates(Integer category, String term, int size) {
        BusquedaDTO.BusquedaDTOBuilder builder = BusquedaDTO.builder()
                .page(0)
                .size(size)
                .sort(DEFAULT_PRODUCT_SORT);

        if (category != null) {
            builder.categoria(category);
        }
        if (term != null && !term.isBlank()) {
            builder.nombre(term);
        }

        try {
            return new ArrayList<>(productoService.buscarProductoConPaginaOrdenFiltro(builder.build()).getContent());
        } catch (Exception ex) {
            return List.of();
        }
    }

    private String extractProductTarget(String originalMessage, String normalizedMessage) {
        String[] anchors = {
                "precio de", "precios de", "valor de", "cuanto vale", "cuanto cuesta", "cuanto me cuesta"
        };

        for (String anchor : anchors) {
            int index = normalizedMessage.indexOf(anchor);
            if (index >= 0) {
                int start = index + anchor.length();
                if (start < originalMessage.length()) {
                    String raw = originalMessage.substring(start);
                    String cleaned = cleanSearchTerm(raw);
                    if (!cleaned.isBlank()) {
                        return cleaned;
                    }
                }
            }
        }

        return cleanSearchTerm(originalMessage);
    }

    private String extractSearchTermFromBrowse(String originalMessage, String normalizedMessage) {
        String[] anchors = {"de", "sobre", "para"};
        for (String anchor : anchors) {
            String token = " " + anchor + " ";
            int index = normalizedMessage.indexOf(token);
            if (index >= 0) {
                int start = index + token.length();
                if (start < originalMessage.length()) {
                    String candidate = cleanSearchTerm(originalMessage.substring(start));
                    if (!candidate.isBlank()) {
                        return candidate;
                    }
                }
            }
        }

        if (containsAny(normalizedMessage, "coca", "pepsi", "cerveza", "jugo", "limonada")) {
            return cleanSearchTerm(originalMessage);
        }

        return null;
    }

    private String cleanSearchTerm(String raw) {
        if (raw == null) {
            return "";
        }

        String normalized = normalize(raw)
                .replaceAll("[^a-z0-9\\s-]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (normalized.isBlank()) {
            return "";
        }

        List<String> tokens = splitTokens(normalized).stream()
                .filter(token -> !STOP_WORDS.contains(token))
                .toList();

        if (tokens.isEmpty()) {
            return "";
        }

        return String.join(" ", tokens);
    }

    private List<String> splitTokens(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value.split("\\s+"));
    }

    private Integer detectCategoryFromMessage(String normalizedMessage) {
        if (isRefreshingIntent(normalizedMessage) || containsAny(normalizedMessage, "bebida", "bebidas", "jugo", "jugos", "refresco", "refrescos", "gaseosa", "cerveza")) {
            return CATEGORY_DRINK;
        }
        if (containsAny(normalizedMessage, "snack", "snacks", "pasaboca", "pasabocas", "comida", "comidas", "almuerzo", "cena", "desayuno")) {
            return CATEGORY_FOOD;
        }
        if (containsAny(normalizedMessage, "servicio", "servicios", "carpa", "sombrilla", "alquiler", "moto")) {
            return CATEGORY_SERVICE;
        }
        return null;
    }

    private boolean isRefreshingIntent(String normalizedMessage) {
        return containsAny(normalizedMessage, "refrescante", "refrescantes", "frio", "fria", "frios", "frias", "calor", "hidrat")
                || containsAny(normalizedMessage, "bebidas frias", "algo frio", "algo para el calor");
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
        return containsAny(normalizedMessage, "recom", "suger", "opcion", "opciones", "que me recomiendas");
    }

    private boolean isProductBrowseIntent(String normalizedMessage) {
        return containsAny(normalizedMessage,
                "producto", "productos", "que tienen", "tienen", "muestrame", "muestreme", "mostrar", "hay",
                "bebidas", "comida", "snacks", "refrescantes", "frio", "calor", "servicios");
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

    private record ProductQuery(
            String searchTerm,
            Integer category,
            List<String> keywordHints,
            String responseTitle,
            boolean preferNaturalResponse
    ) {}

    private record ProductScore(Producto product, int score) {}

    public record IntentResolution(
            String intent,
            boolean handled,
            String response,
            String modelContext,
            boolean preferNaturalResponse
    ) {
        public static IntentResolution handled(String intent, String response) {
            return new IntentResolution(intent, true, response, null, false);
        }

        public static IntentResolution handledWithContext(String intent, String response, String modelContext, boolean preferNaturalResponse) {
            return new IntentResolution(intent, true, response, modelContext, preferNaturalResponse);
        }

        public static IntentResolution fallback(String intent) {
            return new IntentResolution(intent, false, null, null, false);
        }
    }
}
