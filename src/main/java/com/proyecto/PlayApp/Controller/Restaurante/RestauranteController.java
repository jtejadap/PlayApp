package com.proyecto.PlayApp.Controller.Restaurante;

import com.proyecto.PlayApp.entity.Restaurante;
import com.proyecto.PlayApp.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/restaurantes")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    @Autowired
    private AuthService authService;

    @Autowired
    private PlatoService platoService;

    @Autowired
    private BebidaService bebidaService;

    @Autowired
    private ServicioService servicioService;

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("restaurante", new Restaurante());
        return "Restaurante/register"; // Asegúrate de que esta vista es accesible sin autenticación
    }

    @PostMapping("/api/restaurantes/registrar")
    public String registrarRestaurante(@ModelAttribute Restaurante restaurante, Model model) {
        try {
            // Verifica si el correo ya está en uso
            if (authService.existePorCorreoRestaurante(restaurante.getCorreoRestaurante())) {
                model.addAttribute("error", "El correo ya está en uso.");
                return "Restaurante/register"; // Mantener en la página de registro para mostrar el mensaje de error
            }
            restauranteService.registrarRestaurante(restaurante);
            model.addAttribute("registroExitoso", "Registro completado. ¡Ahora puedes iniciar sesión!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "Restaurante/register";
    }

    @PostMapping("/api/restaurantes/login")
    public String login(@RequestParam("correoRestaurante") String correoRestaurante,
                        @RequestParam("password") String password,
                        Model model, HttpSession session) {
        Optional<Restaurante> restaurante = authService.authenticateRestaurante(correoRestaurante, password);
        if (restaurante.isPresent()) {
            // Crear sesión y agregar atributos
            session.setAttribute("nombreRestaurante", restaurante.get().getNombre());
            return "redirect:/restaurantes/admin-restaurante";
        } else {
            model.addAttribute("error", "Credenciales incorrectas. Por favor, inténtelo de nuevo.");
            model.addAttribute("restaurante", new Restaurante());
            return "Restaurante/register";
        }
    }

    @GetMapping("/admin-restaurante")
    public String viewAdminPage(HttpSession session, Model model) {
        String nombreRestaurante = (String) session.getAttribute("nombreRestaurante");
        if (nombreRestaurante == null) {
            return "redirect:/login"; // Redirige al login si no hay sesión activa
        }
        model.addAttribute("nombreRestaurante", nombreRestaurante);
        model.addAttribute("platos", platoService.obtenerTodosLosPlatos());
        return "Restaurante/admin-restaurante";
    }

    @PostMapping("/api/restaurantes/logout")
    public String logout(HttpSession session) {
        // Invalidar la sesión actual
        session.invalidate();
        return "redirect:/restaurantes/registro"; // Redirigir a la página de inicio de sesión u otra página
    }
}
