package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.entity.Envio;
import com.proyecto.PlayApp.entity.Pago;
import com.proyecto.PlayApp.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PagoController {
    private final PagoService servicio;

    @GetMapping("/details")
    public String mostrarEnvio() {
        return "login";
    }

    @PostMapping("/proceed")
    public String crearNuevoEnvio(
            Principal userInSession,
            @ModelAttribute("pago") Pago pago,
            Model model
    ){
        servicio.crearPago(userInSession.getName(), pago);
        return "login";
    }
}
