package com.proyecto.PlayApp.configuration;

import com.proyecto.PlayApp.repository.PedidoRepository;
import com.proyecto.PlayApp.repository.ProductoRepository;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import com.proyecto.PlayApp.service.RandonDataService;
import lombok.RequiredArgsConstructor;
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
    @Override
    public void run(String... args) {
        boolean usuariosEmpty = usuarioRepository.count() == 0;
        boolean productosEmpty = productoRepository.count() == 0;
        boolean pedidosEmpty = pedidoRepository.count() == 0;
        boolean productosSinImagen = !productosEmpty && productoRepository.findAll().stream().anyMatch(producto ->
                producto.getImagen() == null || producto.getImagenContentType() == null || producto.getImagenContentType().isBlank()
        );

        if (usuariosEmpty) {
            System.out.println("🚨 No Entidades found in the database.");
        } else {
            System.out.println("✅ Entidades found: " + usuarioRepository.count());
        }

        if (productosEmpty) {
            System.out.println("🚨 No productos found in the database.");
        } else {
            System.out.println("✅ Productos found: " + productoRepository.count());
        }

        if (pedidosEmpty) {
            System.out.println("🚨 No pedidos found in the database.");
        } else {
            System.out.println("✅ Pedidos found: " + pedidoRepository.count());
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
}
