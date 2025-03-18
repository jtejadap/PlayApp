package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.entity.Restaurante;
import com.proyecto.PlayApp.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/manager")
public class ManagementController {

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

    @GetMapping("/dashboard")
    public String viewAdminPage(HttpSession session, Model model) {
        model.addAttribute("nombreRestaurante", "naem");
        model.addAttribute("platos", platoService.obtenerTodosLosPlatos());
        return "Management/admin-restaurante";
    }

    @PostMapping("/api/restaurantes/logout")
    public String logout(HttpSession session) {
        // Invalidar la sesión actual
        session.invalidate();
        return "redirect:/restaurantes/registro"; // Redirigir a la página de inicio de sesión u otra página
    }
}
