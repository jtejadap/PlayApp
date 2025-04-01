package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.entity.Bebida;
import com.proyecto.PlayApp.entity.Producto;
import com.proyecto.PlayApp.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

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
    public String mostrarProductos(Principal principal, Model model) {
        model.addAttribute("nombreRestaurante", principal.getName());
        model.addAttribute("productos", servicio.listarTodoslosProductos());
        return "Management/productos";
    }

    @GetMapping("/product")
    public String mostrarFormularioNuevoProducto(Model model) {
        model.addAttribute("producto", new Producto());
        return "Management/nuevo-producto";
    }

    @PostMapping("/product/new")
    public String crearNuevoProducto(
            @ModelAttribute("producto") Producto producto,
            @RequestParam("archivoimagen") MultipartFile imagen,
            BindingResult bindingResult,
            Model model
    ) throws IOException {

        if (!imagen.isEmpty()) {
            byte[] imagenBytes = imagen.getBytes();
            //TODO save image on redis
        }
        servicio.crearProducto(producto);
        model.addAttribute("success", "Producto añadido al catalogo con exito");
        return "Management/productos";
    }

    @PostMapping("/api/restaurantes/logout")
    public String logout(HttpSession session) {
        // Invalidar la sesión actual
        session.invalidate();
        return "redirect:/restaurantes/registro"; // Redirigir a la página de inicio de sesión u otra página
    }
}
