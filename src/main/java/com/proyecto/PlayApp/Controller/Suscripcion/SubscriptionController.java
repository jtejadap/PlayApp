package com.proyecto.PlayApp.Controller.Suscripcion;

import com.proyecto.PlayApp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SubscriptionController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam("email") String email) {
        // Enviar correo de confirmación
        String subject = "Gracias por suscribirte";
        String body = "¡Gracias por suscribirte a nuestras ofertas exclusivas!";
        emailService.sendEmail(email, subject, body);

        // Redirigir a la página principal con un mensaje (puedes usar redirección o modelo)
        return "redirect:/inicio";
    }
}