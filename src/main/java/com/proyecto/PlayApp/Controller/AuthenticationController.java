package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {

    private final UsuarioService usuarioService;

    public AuthenticationController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String showLogin(){
        return "login";
    }

    @PostMapping("/register")
    public String createNewUser(@ModelAttribute("formulario") Usuario formulario, Model model) {
        try {
            usuarioService.createUser(formulario);
            model.addAttribute("registroExitoso", "Usuario registrado exitosamente. Ahora puedes iniciar sesión.");
            return "login"; // Permanecer en la página de registro para mostrar el mensaje de éxito
        } catch (Exception e) {
            model.addAttribute("error", "Hubo un problema al registrar el usuario. Inténtalo de nuevo.");
            return "login";
        }
    }

    @GetMapping("/signup")
    public String showManagerForm(Model model) {
        model.addAttribute("restaurante", new Usuario());
        return "Management/register-manager"; // Asegúrate de que esta vista es accesible sin autenticación
    }

    @PostMapping("/signup")
    public String createNewRestaurant(@ModelAttribute("formulario") Usuario formulario, Model model) {
        try {
            usuarioService.createRestaurant(formulario);
            model.addAttribute("registroExitoso", "Registro completado. ¡Ahora puedes iniciar sesión!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "login";
    }


}
