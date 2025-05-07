package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.dto.BusquedaDTO;
import com.proyecto.PlayApp.entity.Producto;
import com.proyecto.PlayApp.service.CarritoService;
import com.proyecto.PlayApp.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UsuarioController {
    private final PedidoService pedidos;
    private final CarritoService carrito;

    @GetMapping("/orders")
    public String mostrarMisPedidos(
            Principal usuario,
            Model model
    ){

        model.addAttribute("carrito", numeroItemsCarrito(usuario));
        model.addAttribute("pedidos", pedidos.listarPedidosPorUsuario(usuario.getName()));
        return "user-orders";
    }

    private int numeroItemsCarrito(Principal user){
        if(user == null){
            return 0;
        }
        return carrito.listarCarrito(user.getName()).size();
    }
}
