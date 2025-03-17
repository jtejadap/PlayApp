package com.proyecto.PlayApp.Controller.Restaurante;

import com.proyecto.PlayApp.entity.Pedido;
import com.proyecto.PlayApp.entity.Plato;
import com.proyecto.PlayApp.entity.Bebida;
import com.proyecto.PlayApp.entity.Servicio;
import com.proyecto.PlayApp.service.PedidoService;
import com.proyecto.PlayApp.service.PlatoService;
import com.proyecto.PlayApp.service.BebidaService;
import com.proyecto.PlayApp.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class PedidoAdminController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PlatoService platoService;

    @Autowired
    private BebidaService bebidaService;

    @Autowired
    private ServicioService servicioService;

    // Ruta para listar todos los pedidos
    @GetMapping("/pedidos")
    public String listarPedidos(Model model) {
        List<Pedido> pedidos = pedidoService.obtenerTodosLosPedidos();

        // Añadir logs para depuración
        System.out.println("Número de pedidos encontrados: " + pedidos.size());
        for (Pedido pedido : pedidos) {
            System.out.println("Pedido ID: " + pedido.getId());
            System.out.println("Usuario: " + (pedido.getUsuario() != null ? pedido.getUsuario().getNombre_completo() : "Sin usuario"));
            System.out.println("Total: " + pedido.getTotal());
            System.out.println("Número de items: " + (pedido.getItems() != null ? pedido.getItems().size() : "0"));
        }

        model.addAttribute("pedidos", pedidos);
        return "Restaurante/admin-pedidos";
    }

    @PostMapping("/pedidos/eliminar/{id}")
    public String eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return "redirect:/admin/pedidos"; // Redirige a la lista de pedidos después de eliminar
    }

    // Ruta para listar todos los productos
    @GetMapping("/productos")
    public String listarProductos(Model model) {
        List<Plato> platos = platoService.obtenerTodosLosPlatos();
        List<Bebida> bebidas = bebidaService.obtenerTodasLasBebidas();
        List<Servicio> servicios = servicioService.obtenerTodosLosServicios();

        model.addAttribute("platos", platos);
        model.addAttribute("bebidas", bebidas);
        model.addAttribute("servicios", servicios);
        return "Restaurante/admin-productos";
    }
}
