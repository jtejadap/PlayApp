package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.dto.ItemDTO;
import com.proyecto.PlayApp.entity.CarritoItem;
import com.proyecto.PlayApp.service.CarritoService;
import com.proyecto.PlayApp.service.PedidoService;
import com.proyecto.PlayApp.service.UsuarioService;

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
    private final UsuarioService usuarios;


    @GetMapping("/add/{item}")
    public String addItem(
            @PathVariable  String item,
            @RequestParam(name = "cantidad", defaultValue = "1") Integer cantidad,
            Principal usuario
    ) {
        try{
            servicio.agregar(
                    ItemDTO.builder()
                            .itemId(item)
                            .cantidad(cantidad)
                            .correo(usuario.getName())
                            .build()
            );
            return "redirect:/shop?status=true";

        } catch (Exception e) {
            return "redirect:/shop?error=true";
        }
    }

    @GetMapping("/rm/{item}")
    public String removeItem(
            @PathVariable  String item,
            Principal usuario
    ) {
        try{
            servicio.eliminar(
                    usuario.getName(),
                    item
            );
            return "redirect:/cart/checkout";

        } catch (Exception e) {
            return "redirect:/cart/checkout";
        }
    }

    @GetMapping("/checkout")
    public String visualizarCarritoDeCompra(Principal usuario, Model model) {
        List<CarritoItem> checkout = servicio.listarCarrito(usuario.getName());
        double total = checkout.stream().mapToDouble(item-> item.getCantidad()* item.getPrecio()).sum();
        model.addAttribute("total", total);
        model.addAttribute("carrito", checkout.size());
        model.addAttribute("checkout", checkout);
        String nombreusuario = "";
        if(usuario != null){
        nombreusuario = usuarios.buscarUsuario(usuario.getName()).getNombreCompleto();
        }

        model.addAttribute("nombreUsuario", nombreusuario);
        return "checkout";
    }

    @GetMapping("/checkout/proceed")
    public String procederCompra(Principal usuario, Model model) {
        try {
            List<CarritoItem> checkout = servicio.listarCarrito(usuario.getName());
            if(checkout.isEmpty()){
                throw new RuntimeException("El carrito esta vacio, dirijase a la tienda para llenarlo.");
            }
            return "redirect:/shipment/place";
        }catch (Exception e){
            model.addAttribute("error", e.getMessage());
            return "pedido-confirmacion";
        }
    }


}
