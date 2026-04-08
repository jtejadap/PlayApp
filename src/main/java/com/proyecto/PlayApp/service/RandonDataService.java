package com.proyecto.PlayApp.service;

import com.github.javafaker.Faker;
import com.proyecto.PlayApp.dto.DetallesDTO;
import com.proyecto.PlayApp.entity.*;
import com.proyecto.PlayApp.repository.PedidoRepository;
import com.proyecto.PlayApp.repository.ProductoRepository;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.Binary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.sample;

@Service
@RequiredArgsConstructor
public class RandonDataService {
    private static final int TOTAL_PRODUCTOS = 10;
    private static final int TOTAL_PEDIDOS = 100;
    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository ;
    private final PedidoRepository pedidoRepository;

    private static final int BATCH_SIZE = 100;
    private final Faker faker = new Faker(new Locale("es"));
    private final Random random = new Random();
    private static final LocalDate START_DATE = LocalDate.of(2020, 1, 1);
    private static final LocalDate END_DATE = LocalDate.now();
    private static final List<ProductoBase> PRODUCTOS_COSTEROS = List.of(
            new ProductoBase("Pescado frito", "Pescado frito con ensalada fresca y arroz de coco.", 32000.0, 25.0, 4, 1, "static/img/catalogo/pescado-frito.jpg"),
            new ProductoBase("Arroz de coco", "Arroz de coco tradicional del Caribe colombiano.", 12000.0, 40.0, 1, 1, "static/img/catalogo/arroz-coco.jpg"),
            new ProductoBase("Patacones con hogao", "Patacones crujientes acompanados con hogao casero.", 14000.0, 35.0, 1, 1, "static/img/catalogo/patacones-hogao.jpg"),
            new ProductoBase("Ceviche de camaron", "Ceviche de camaron fresco con limon y galletas saladas.", 28000.0, 20.0, 4, 1, "static/img/catalogo/ceviche-camaron.webp"),
            new ProductoBase("Arepa de huevo", "Arepa de huevo cartagenera recien preparada.", 9000.0, 30.0, 1, 1, "static/img/catalogo/arepa-huevo.jpg"),
            new ProductoBase("Mojarra con patacones", "Mojarra frita servida con patacones y ensalada.", 35000.0, 18.0, 4, 1, "static/img/catalogo/mojarra-patacones.webp"),
            new ProductoBase("Limonada de coco", "Bebida fria de coco y limon ideal para la playa.", 10000.0, 50.0, 4, 2, "static/img/catalogo/limonada-coco.jpg"),
            new ProductoBase("Coco frio", "Coco frio natural servido al instante.", 8000.0, 45.0, 4, 2, "static/img/catalogo/coco-frio.jpg"),
            new ProductoBase("Jugo de maracuya", "Jugo natural de maracuya bien frio.", 9000.0, 45.0, 4, 2, "static/img/catalogo/jugo-maracuya.jpg"),
            new ProductoBase("Cerveza nacional", "Cerveza fria para disfrutar frente al mar.", 7000.0, 60.0, 1, 2, "static/img/catalogo/cerveza-nacional.webp")
    );

    public void seedDatabase(){
        generateUsuarios("ROLE_ADMIN", 10);
        generateUsuarios("ROLE_USER", 1000);
        generateProductosForAllUsers();
        generatePedidosForUsersWithRoleUser(TOTAL_PEDIDOS);
    }

    public void generateUsuarios(String rol, int cantidad) {
        int batchsize = 5;
        if(cantidad > 100){
            batchsize = 100;
        }
        for (int i = 0; i < cantidad; i += batchsize) {
            List<Usuario> usuarios = new ArrayList<>();
            int registrosLote = Math.min(batchsize, cantidad - i);

            for (int j = 0; j < registrosLote; j++) {
                Usuario usuario = Usuario.builder()
                        .nombreCompleto(faker.name().fullName())
                        .correo(generateUniqueEmail(faker))
                        .password(passwordEncoder.encode("Password123"))
                        .rol(rol)
                        .build();
                usuarios.add(usuario);
            }

            mongoTemplate.insertAll(usuarios);
            System.out.println("Se insertaron " + usuarios.size() + " registros de tipo: " +rol);
        }

        System.out.println("Se generaron " + cantidad + " registros de tipo: " + rol);
    }

    private String generateUniqueEmail(Faker faker) {
        String username = faker.name().username().replaceAll("[^a-zA-Z0-9]", "");
        return username.toLowerCase() + "@" + faker.internet().domainName();
    }

    public void generateProductosForAllUsers() {
        List<Usuario> usuarios = usuarioRepository.findByRolContaining("ROLE_ADMIN");
        if (usuarios.isEmpty()) {
            System.out.println("No hay administradores para asociar productos.");
            return;
        }

        Map<String, Producto> productosExistentes = new HashMap<>();
        for (Producto producto : productoRepository.findAll()) {
            productosExistentes.put(producto.getNombre(), producto);
        }

        List<Producto> allProductos = new ArrayList<>();

        for (int i = 0; i < TOTAL_PRODUCTOS; i++) {
            ProductoBase productoBase = PRODUCTOS_COSTEROS.get(i);
            Usuario usuario = usuarios.get(i % usuarios.size());
            Producto producto = productosExistentes.getOrDefault(productoBase.nombre(), new Producto());
            producto.setNombre(productoBase.nombre());
            producto.setDescripcion(productoBase.descripcion());
            producto.setPrecio(productoBase.precio());
            producto.setStock(productoBase.stock());
            producto.setTipo(productoBase.tipo());
            producto.setCategoria(productoBase.categoria());
            producto.setImagen(loadImage(productoBase.imagePath()));
            producto.setImagenContentType(detectContentType(productoBase.imagePath()));
            producto.setEntidad(usuario);
            allProductos.add(producto);
        }

        productoRepository.saveAll(allProductos);
        System.out.println("Se generaron " + allProductos.size() + " productos costeros para PlayApp.");
    }

    public void generatePedidosForUsersWithRoleUser(int totalPedidos) {
        List<Usuario> usuarios = usuarioRepository.findByRolContaining("ROLE_USER");
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios con ROLE_USER para generar pedidos.");
            return;
        }

        List<Pedido> batchPedidos = new ArrayList<>(BATCH_SIZE);

        for (int i = 0; i < totalPedidos; i++) {
            Usuario user = usuarios.get(i % usuarios.size());
            DetallesDTO generateFakeCarrito = generateFakeCarrito();
            double total = generateFakeCarrito.getCarrito().stream().mapToDouble(PedidoItem::getSubtotal).sum();
            LocalDateTime date = generateRandomDateTimeBetween(START_DATE, END_DATE);

            Pedido pedido = Pedido.builder()
                    .estado(faker.number().numberBetween(0, 3))
                    .total(total)
                    .timestamp(date)
                    .carrito(generateFakeCarrito.getCarrito())
                    .restaurantes(generateFakeCarrito.getRestaurantes())
                    .cliente(user)
                    .pago(generateFakePago(total,date,user.getId()))
                    .envio(generateFakeEnvio(date,user.getId()))
                    .build();

            batchPedidos.add(pedido);
            if (batchPedidos.size() >= BATCH_SIZE) {
                pedidoRepository.saveAll(batchPedidos);
                batchPedidos.clear();
                System.out.println("Se generaron " + BATCH_SIZE + " registros para los pedidos.");
            }
        }

        if (!batchPedidos.isEmpty()) {
            pedidoRepository.saveAll(batchPedidos);
        }
        System.out.println("Se generaron " + totalPedidos + " registros para los pedidos.");
    }

    private DetallesDTO generateFakeCarrito() {
        List<PedidoItem> items = new ArrayList<>();
        Set<String> restaurantes = new HashSet<>();
        int itemCount = faker.number().numberBetween(1, 6);
        for (int i = 0; i < itemCount; i++) {
            PedidoItem item = new PedidoItem();
            Producto producto = getRandomRecord(Producto.class, "productos").orElse(new Producto());
            item.setProducto(producto);
            item.setCantidad(faker.number().numberBetween(1, 10));
            item.setSubtotal(item.getCantidad()*producto.getPrecio());
            items.add(item);
            restaurantes.add(producto.getEntidad().getId());
        }

        return  DetallesDTO.builder()
                .carrito(items)
                .restaurantes(new ArrayList<>(restaurantes))
                .build();
    }

    private Pago generateFakePago(double valor, LocalDateTime fecha, String cliente) {
        return Pago.builder()
                .metodo(faker.options().option("PAYPAL", "DEBITO", "CREDITO"))
                .valor(valor)
                .estado(random.nextInt(3)) // Random estado between 0 and 2
                .fecha(fecha)
                .clienteId(cliente)
                .build();
    }

    private Envio generateFakeEnvio(LocalDateTime fecha, String cliente) {
        return Envio.builder()
                .latitud(Double.valueOf(faker.address().latitude().replace(",", ".")))
                .longitud(Double.valueOf(faker.address().longitude().replace(",", ".")))
                .dirreccion(faker.address().fullAddress())
                .descripcion(faker.lorem().sentence())
                .mesa(random.nextInt(20) + 1) // Random mesa between 1 and 20
                .carpa(random.nextInt(10) + 1) // Random carpa between 1 and 10
                .fecha(fecha) // Random date in the past 30 days
                .clienteId(cliente)
                .build();
    }

    public <T> Optional<T> getRandomRecord(Class<T> entityClass, String collectionName) {
        Aggregation aggregation = Aggregation.newAggregation(sample(1)); // Sample 1 random record
        AggregationResults<T> results = mongoTemplate.aggregate(aggregation, collectionName, entityClass);

        return results.getMappedResults().stream().findFirst();
    }

    private LocalDateTime generateRandomDateTimeBetween(LocalDate start, LocalDate end) {
        long daysBetween = ChronoUnit.DAYS.between(start, end);
        long randomDays = faker.number().numberBetween(0, (int) daysBetween);
        LocalDate randomDate = start.plusDays(randomDays);
        LocalTime randomTime = LocalTime.of(faker.number().numberBetween(0, 24), faker.number().numberBetween(0, 60));
        return LocalDateTime.of(randomDate, randomTime);
    }

    private Binary loadImage(String imagePath) {
        try {
            ClassPathResource resource = new ClassPathResource(imagePath);
            return new Binary(resource.getInputStream().readAllBytes());
        } catch (IOException e) {
            System.out.println("No se pudo cargar la imagen del producto: " + imagePath);
            return null;
        }
    }

    private String detectContentType(String imagePath) {
        String normalized = imagePath.toLowerCase(Locale.ROOT);
        if (normalized.endsWith(".webp")) {
            return "image/webp";
        }
        if (normalized.endsWith(".png")) {
            return "image/png";
        }
        if (normalized.endsWith(".jpg") || normalized.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }

    private record ProductoBase(
            String nombre,
            String descripcion,
            Double precio,
            Double stock,
            Integer tipo,
            Integer categoria,
            String imagePath
    ) {
    }

}
