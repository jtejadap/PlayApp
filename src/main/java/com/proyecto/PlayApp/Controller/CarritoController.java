package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.dto.ItemDTO;
import com.proyecto.PlayApp.entity.Producto;
import com.proyecto.PlayApp.service.CarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CarritoController {
    private final CarritoService servicio;

    @GetMapping("/add/{item}")
    public String addItem(
            @PathVariable  Long item,
            @RequestParam(name = "cantidad", defaultValue = "1") Integer cantidad,
            Principal usuario
    ) {
        if(usuario==null){
            return "redirect:/login";
        }
        servicio.agregar(
                ItemDTO.builder()
                        .itemId(item)
                        .cantidad(cantidad)
                        .correo(usuario.getName())
                        .build()
        );
        return "redirect:/shop?status=true";
    }

    @GetMapping("/checkout")
    public String visualizarCarritoDeCompra(Principal usuario, Model model) {
        List<Producto> checkout = servicio.obtenerCheckout(usuario.getName());
        model.addAttribute("carrito", checkout);
        return "pedido-confirmacion";
    }


}
