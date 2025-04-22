package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.entity.Envio;
import com.proyecto.PlayApp.service.EnvioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shipment")
public class EnvioController {
    private final EnvioService servicio;

    @GetMapping("/place")
    public String mostrarEnvio() {
        return "login";
    }

    @PostMapping("/proceed")
    public String crearNuevoEnvio(
            Principal userInSession,
            @ModelAttribute("envio") Envio envio,
            Model model
    ){
        servicio.crearEnvio(userInSession.getName(), envio);
        return "login";
    }
}
