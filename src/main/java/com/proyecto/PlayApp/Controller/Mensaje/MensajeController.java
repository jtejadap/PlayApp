package com.proyecto.PlayApp.Controller.Mensaje;

import com.proyecto.PlayApp.entity.Mensaje;
import com.proyecto.PlayApp.repository.MensajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MensajeController {

    @Autowired
    private MensajeRepository mensajeRepository;

    @PostMapping("/contacto/enviar")
    public String enviarMensaje(@RequestParam("nombre") String nombre,
                                @RequestParam("email") String email,
                                @RequestParam("asunto") String asunto,
                                @RequestParam("mensaje") String mensaje,
                                RedirectAttributes redirectAttributes) {
        // Guardar el mensaje en la base de datos
        Mensaje nuevoMensaje = new Mensaje();
        nuevoMensaje.setNombre(nombre);
        nuevoMensaje.setEmail(email);
        nuevoMensaje.setAsunto(asunto);
        nuevoMensaje.setMensaje(mensaje);
        mensajeRepository.save(nuevoMensaje);

        // Añadir un atributo de éxito para mostrar un mensaje de confirmación
        redirectAttributes.addFlashAttribute("mensajeEnviado", "¡Mensaje enviado con éxito!");

        // Redirigir de nuevo a la página de contacto
        return "redirect:/contacto";
    }
}