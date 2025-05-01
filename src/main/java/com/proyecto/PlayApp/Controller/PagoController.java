package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.entity.Envio;
import com.proyecto.PlayApp.entity.Pago;
import com.proyecto.PlayApp.service.CarritoService;
import com.proyecto.PlayApp.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PagoController {
    private final PagoService servicio;
    private final CarritoService carritos;

    @GetMapping("/paymentgateway")
    public String mostarPasarelaDePagos(
            @RequestParam(name = "valor", required = false) String valor,
            @RequestParam(name = "metodo", required = false) String metodo,
            Model model
    ) {
        model.addAttribute("valor", valor);
        model.addAttribute("metodo", metodo);
        return "fake-payment-gateway";
    }

    @GetMapping("/proceed")
    public String terminarPago(
            Principal userInSession,
            @RequestParam(name = "estado", required = false) String estado,
            Model model
    ){
        if(!estado.equalsIgnoreCase("SUCCESS")){
            model.addAttribute("error", "Ha ocurrido un error al procesar el pago por favor revise la sección de pedidos para intentar nuevamente el pago.");
            return "pedido-confirmacion";
        }
        model.addAttribute("success", "Compra realizada con exito!");
        model.addAttribute("pedidos", carritos.listarCarrito(userInSession.getName()));
        carritos.limpiar(userInSession.getName());
        return "pedido-confirmacion";
    }
}
