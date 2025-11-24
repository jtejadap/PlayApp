package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.service.PaymentService;

import java.math.BigDecimal;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/mercadopago")
public class PaymentController {


    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/mercadopago")
    public String mercadopago(
            @RequestParam("valor") double valor,
            @RequestParam("orden") String orden
    ) {
        try {
            String url = paymentService.crearPreferencia(
                    "Pedido #" + orden,
                    new BigDecimal(valor),
                    1,
                    orden
            );

            return "redirect:" + url;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR MERCADOPAGO: " + e.getMessage());
            // return "redirect:/payment/paymentgateway?valor=" + valor + "&orden=" + orden + "&error=1";
        }
    }

    // @GetMapping("/proceed")
    // public String terminarPago(
    //         Principal userInSession,
    //         @RequestParam(name = "estado", required = false) String estado,
    //         @RequestParam(name = "orden", required = false) String orden,
    //         Model model
    // ){
    //     carritos.limpiar(userInSession.getName());
    //     model.addAttribute("pedidos", carritos.listarCarrito(userInSession.getName()));
    //     if(!estado.equalsIgnoreCase("SUCCESS")){
    //         model.addAttribute("error", "Ha ocurrido un error...");
    //         pedidos.actualizarEstadoPagoPedido(2, orden);
    //         return "pedido-confirmacion";
    //     }

    //     pedidos.actualizarEstadoPagoPedido(1, orden);
    //     model.addAttribute("success", "Compra realizada con exito!");

    //     return "pedido-confirmacion";
    // }
}
