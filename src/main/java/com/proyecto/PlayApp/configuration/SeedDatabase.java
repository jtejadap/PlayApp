package com.proyecto.PlayApp.configuration;

import com.proyecto.PlayApp.repository.PedidoRepository;
import com.proyecto.PlayApp.repository.ProductoRepository;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import com.proyecto.PlayApp.service.RandonDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeedDatabase implements CommandLineRunner {
    private static final int TOTAL_ADMINS = 10;
    private static final int TOTAL_USERS = 10;

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final RandonDataService randonDataService;

    @Value("${playapp.seed.force:false}")
    private boolean forceSeed;

    @Value("${playapp.seed.admins:10}")
    private int seedAdmins;

    @Value("${playapp.seed.users:10}")
    private int seedUsers;

    @Value("${playapp.seed.pedidos:100}")
    private int seedPedidos;

    @Override
    public void run(String... args) {
        boolean usuariosEmpty = usuarioRepository.count() == 0;
        boolean productosEmpty = productoRepository.count() == 0;
        boolean pedidosEmpty = pedidoRepository.count() == 0;
        boolean productosSinImagen = !productosEmpty && productoRepository.findAll().stream().anyMatch(producto ->
                producto.getImagen() == null || producto.getImagenContentType() == null || producto.getImagenContentType().isBlank()
        );

        logCollectionState("Entidades", usuariosEmpty, usuarioRepository.count());
        logCollectionState("Productos", productosEmpty, productoRepository.count());
        logCollectionState("Pedidos", pedidosEmpty, pedidoRepository.count());

        if (forceSeed) {
            System.out.println("Seeder manual activado. Generando datos demo en la base conectada.");
            randonDataService.generateUsuarios("ROLE_ADMIN", seedAdmins);
            randonDataService.generateUsuarios("ROLE_USER", seedUsers);
            randonDataService.generateProductosForAllUsers();
            randonDataService.generatePedidosForUsersWithRoleUser(seedPedidos);
            return;
        }

        if (usuariosEmpty || productosEmpty || pedidosEmpty || productosSinImagen) {
            System.out.println("Missing initial data detected, running seeder.");

            if (usuariosEmpty) {
                randonDataService.generateUsuarios("ROLE_ADMIN", TOTAL_ADMINS);
                randonDataService.generateUsuarios("ROLE_USER", TOTAL_USERS);
            }

            if (productosEmpty || productosSinImagen) {
                randonDataService.generateProductosForAllUsers();
            }

            if (pedidosEmpty) {
                randonDataService.generatePedidosForUsersWithRoleUser(100);
            }
        }
    }

    private void logCollectionState(String collection, boolean empty, long count) {
        if (empty) {
            System.out.println("No " + collection + " found in the database.");
            return;
        }
        System.out.println(collection + " found: " + count);
    }
}
