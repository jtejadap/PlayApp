package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.dto.ItemDTO;
import com.proyecto.PlayApp.entity.CarritoItem;
import com.proyecto.PlayApp.service.CarritoService;
import com.proyecto.PlayApp.service.PedidoService;
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
    private final PedidoService pedidos;

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
        List<CarritoItem> checkout = servicio.listarCarrito(usuario.getName());
        double total = checkout.stream().mapToDouble(item-> item.getCantidad()* item.getPrecio()).sum();
        model.addAttribute("total", total);
        model.addAttribute("carrito", checkout.size());
        model.addAttribute("checkout", checkout);
        return "checkout";
    }

    @GetMapping("/checkout/proceed")
    public String procederCompra(Principal usuario, Model model) {
        try {
            List<CarritoItem> checkout = servicio.listarCarrito(usuario.getName());
            pedidos.crearPedido(usuario.getName(), checkout);
            servicio.limpiar(usuario.getName());
            model.addAttribute("pedidos", checkout);
            model.addAttribute("success", "¡Compra realizada con exito!, en breve recibirás tu pedido.");
            return "pedido-confirmacion";
        }catch (Exception e){
            model.addAttribute("error", e.getMessage());
            return "pedido-confirmacion";
        }

    }


}
