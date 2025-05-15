package com.proyecto.PlayApp.service;

import com.github.javafaker.Faker;
import com.proyecto.PlayApp.dto.DetallesDTO;
import com.proyecto.PlayApp.entity.*;
import com.proyecto.PlayApp.repository.PedidoRepository;
import com.proyecto.PlayApp.repository.ProductoRepository;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.sample;

@Service
@RequiredArgsConstructor
public class RandonDataService {
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

    public void seedDatabase(){
        generateUsuarios("ROLE_ADMIN", 10);
        generateUsuarios("ROLE_USER", 1000);
        generateProductosForAllUsers(5);
        generatePedidosForUsersWithRoleUser(15);
    }

    public void generateUsuarios(String rol, int cantidad) {
        int batchsize = 5;
        if(cantidad > 100){
            batchsize = 100;
        }
        for (int i = 0; i < cantidad; i += batchsize) {
            List<Usuario> usuarios = new ArrayList<>();

            for (int j = 0; j < batchsize; j++) {
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

    public void generateProductosForAllUsers(int productosPerUser) {
        List<Usuario> usuarios = usuarioRepository.findByRolContaining("ROLE_ADMIN");
        List<Producto> allProductos = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            for (int i = 0; i < productosPerUser; i++) {
                Producto producto = new Producto();
                producto.setNombre(faker.commerce().productName());
                producto.setDescripcion(faker.lorem().sentence());
                producto.setPrecio(Double.parseDouble(faker.commerce().price().replace(",", ".")));
                producto.setStock((double) faker.number().numberBetween(1, 100));
                producto.setTipo(faker.number().numberBetween(1, 5));
                producto.setCategoria(faker.number().numberBetween(1, 4));
                producto.setImagen(null); // or generate a Binary image if needed
                producto.setEntidad(usuario);
                allProductos.add(producto);
            }
        }

        productoRepository.saveAll(allProductos);
        System.out.println("Se generaron  registros para los productos.");
    }

    public void generatePedidosForUsersWithRoleUser(int pedidosPerUser) {
        List<Usuario> usuarios = usuarioRepository.findByRolContaining("ROLE_USER");
        List<Pedido> batchPedidos = new ArrayList<>(BATCH_SIZE);

        for (Usuario user : usuarios) {
            for (int i = 0; i < pedidosPerUser; i++) {
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
                    System.out.println("Se generaron "+BATCH_SIZE +" registros para los pedidos.");
                }
            }
        }
        if (!batchPedidos.isEmpty()) {
            pedidoRepository.saveAll(batchPedidos);
        }
        System.out.println("Se generaron registros para los Pedidos.");
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


}
