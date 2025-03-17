package com.proyecto.PlayApp.Controller.Cliente;

import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.service.AuthService;
import com.proyecto.PlayApp.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String irLogin(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "login-register"; // Vista combinada para login y registro
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email, @RequestParam("passwordU") String passwordU,
                        HttpSession session, Model model) {
        Optional<Usuario> usuarioOpt = authService.authenticateUsuario(email, passwordU);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            session.setAttribute("usuarioLogueado", usuario);
            session.setAttribute("nombreUsuario", usuario.getNombre_completo());
            return "redirect:/inicio"; // Redirige al home después del login
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            model.addAttribute("usuario", new Usuario());
            return "login-register";
        }
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario, Model model) {
        try {
            // Verifica si el correo ya está en uso
            if (usuarioService.existePorCorreo(usuario.getCorreo())) {
                model.addAttribute("error", "El correo ya está en uso.");
                return "login-register"; // Mantener en la página de registro para mostrar el mensaje de error
            }
            usuarioService.guardarUsuario(usuario);
            model.addAttribute("registroExitoso", "Usuario registrado exitosamente. Ahora puedes iniciar sesión.");
            return "login-register"; // Permanecer en la página de registro para mostrar el mensaje de éxito
        } catch (Exception e) {
            model.addAttribute("error", "Hubo un problema al registrar el usuario. Inténtalo de nuevo.");
            return "login-register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}
