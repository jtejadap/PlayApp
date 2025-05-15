package com.proyecto.PlayApp.Controller.Mensaje;

import com.proyecto.PlayApp.entity.Mensaje;
import com.proyecto.PlayApp.repository.MensajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/contacto")
@RequiredArgsConstructor
public class MensajeController {

    @Autowired
    private MensajeRepository mensajeRepo;

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("mensaje", new Mensaje()); // Cambiado a "mensajeContacto"
        return "contacto";
    }

    @PostMapping("/enviar")
    public String enviarMensaje(@ModelAttribute("mensajeContacto") Mensaje mensaje) {
         // Ahora funcionará correctamente
        mensajeRepo.save(mensaje);
        return "redirect:/resena";
    }
}