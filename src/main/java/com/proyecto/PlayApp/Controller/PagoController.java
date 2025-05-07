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
    private final PagoService pagos;
    private final CarritoService carritos;

    @GetMapping("/paymentgateway")
    public String mostarPasarelaDePagos(
            @RequestParam(name = "valor", required = false) String valor,
            @RequestParam(name = "metodo", required = false) String metodo,
            @RequestParam(name = "orden", required = false) Long orden,
            Model model
    ) {
        model.addAttribute("valor", valor);
        model.addAttribute("metodo", metodo);
        model.addAttribute("orden", orden);
        return "fake-payment-gateway";
    }

    @GetMapping("/proceed")
    public String terminarPago(
            Principal userInSession,
            @RequestParam(name = "estado", required = false) String estado,
            @RequestParam(name = "orden", required = false) Long orden,
            Model model
    ){
        carritos.limpiar(userInSession.getName());

        if(!estado.equalsIgnoreCase("SUCCESS")){
            model.addAttribute("error", "Ha ocurrido un error al procesar el pago por favor revise la sección de pedidos para intentar nuevamente el pago.");
            pagos.actualizarEstadoPago(orden,2);
            return "pedido-confirmacion";
        }

        model.addAttribute("success", "Compra realizada con exito!");
        model.addAttribute("pedidos", carritos.listarCarrito(userInSession.getName()));
        pagos.actualizarEstadoPago(orden,1);
        return "pedido-confirmacion";
    }
}
