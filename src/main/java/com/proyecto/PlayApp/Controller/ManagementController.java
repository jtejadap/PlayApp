package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager")
public class ManagementController {
    private final ProductoService servicio;

    public ManagementController(ProductoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/dashboard")
    public String viewAdminPage(HttpSession session, Model model) {
        model.addAttribute("nombreRestaurante", "naem");
        model.addAttribute("platos", servicio.listarTodoslosProductos());
        return "Management/dashboard";
    }

    @GetMapping("/products")
    public String mostrarProductos(Model model) {
        model.addAttribute("nombreRestaurante", "naem");
        model.addAttribute("platos", servicio.listarTodoslosProductos());
        return "Management/productos";
    }

    @PostMapping("/api/restaurantes/logout")
    public String logout(HttpSession session) {
        // Invalidar la sesión actual
        session.invalidate();
        return "redirect:/restaurantes/registro"; // Redirigir a la página de inicio de sesión u otra página
    }
}
