package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.dto.CompraDTO;
import com.proyecto.PlayApp.dto.EnvioPagoDTO;
import com.proyecto.PlayApp.entity.CarritoItem;
import com.proyecto.PlayApp.service.CarritoService;
import com.proyecto.PlayApp.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shipment")
public class EnvioController {
    private final PedidoService pedidos;
    private final CarritoService carritos;

    @GetMapping("/place")
    public String mostrarEnvio(
            Principal userInSession,
            Model model
    ) {
        List<CarritoItem> checkout = carritos.listarCarrito(userInSession.getName());
        model.addAttribute("carrito", checkout.size());
        model.addAttribute("envioPago", new EnvioPagoDTO());
        return "shipment-payment";
    }

    @PostMapping("/proceed")
    public String crearNuevoEnvio(
            Principal userInSession,
            @ModelAttribute("envioPago") EnvioPagoDTO envioPago,
            Model model
    ){
        try{
            List<CarritoItem> items = carritos.listarCarrito(userInSession.getName());
            CompraDTO compra = CompraDTO.builder()
                    .correoUsuario(userInSession.getName())
                    .carrito(items)
                    .envioPago(envioPago)
                    .build();

            pedidos.RealizarPedido(compra);
            model.addAttribute("success", "Compra realizada con exito!");
            model.addAttribute("pedidos", items);
            carritos.limpiar(userInSession.getName());
            return "pedido-confirmacion";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "pedido-confirmacion";
        }
    }
}
