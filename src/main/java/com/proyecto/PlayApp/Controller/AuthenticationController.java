package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthenticationController {

    private final UsuarioService usuarioService;

    public AuthenticationController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String showLogin(Model model) {
        if (!model.containsAttribute("formulario")) {
            model.addAttribute("formulario", new Usuario());
        }
        if (!model.containsAttribute("selectedRole")) {
            model.addAttribute("selectedRole", "USER");
        }
        return "login";
    }

    @PostMapping("/register")
    public String createNewUser(@ModelAttribute("formulario") Usuario formulario,
                                @RequestParam(name = "roleSelection", defaultValue = "USER") String roleSelection,
                                Model model) {
        try {
            usuarioService.crearUsuarioPorRol(formulario, roleSelection);
            model.addAttribute("formulario", new Usuario());
            model.addAttribute("selectedRole", roleSelection);
            model.addAttribute("registroExitoso", "Registro completado. Ahora puedes iniciar sesion.");
        } catch (Exception e) {
            model.addAttribute("formulario", formulario);
            model.addAttribute("selectedRole", roleSelection);
            model.addAttribute("error", e instanceof IllegalArgumentException
                    ? e.getMessage()
                    : "Hubo un problema al registrar la cuenta. Intentalo de nuevo.");
        }
        return "login";
    }

    @GetMapping("/signup")
    public String showManagerForm(Model model) {
        if (!model.containsAttribute("formulario")) {
            model.addAttribute("formulario", new Usuario());
        }
        model.addAttribute("selectedRole", "ADMIN");
        return "login";
    }

    @PostMapping("/signup")
    public String createNewRestaurant(@ModelAttribute("formulario") Usuario formulario, Model model) {
        return createNewUser(formulario, "ADMIN", model);
    }
}
