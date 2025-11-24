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
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final RandonDataService randonDataService;
    @Override
    public void run(String... args) {
        boolean usuariosEmpty = usuarioRepository.count() == 0;
        boolean productosEmpty = productoRepository.count() == 0;
        boolean pedidosEmpty = pedidoRepository.count() == 0;

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
        if(usuariosEmpty && productosEmpty && pedidosEmpty){
            System.out.println("Clean Database found proceed with Seeder");
            // randonDataService.seedDatabase();
        }
    }
}
