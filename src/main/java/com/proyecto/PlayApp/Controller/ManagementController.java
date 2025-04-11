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
import java.util.Optional;

@Controller
@RequestMapping("/manager")
public class ManagementController {
    private final ProductoService servicio;

    public ManagementController(ProductoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/dashboard")
    public String viewAdminPage(Principal principal, Model model) {
        model.addAttribute("nombreRestaurante", principal.getName());
        return "Management/dashboard";
    }

    @GetMapping("/products")
    public String mostrarProductos(Principal principal, Model model) {
        model.addAttribute("nombreRestaurante", principal.getName());
        model.addAttribute("productos", servicio.listarTodosLosProductosPorRestaurante(principal.getName()));
        return "Management/productos";
    }

    @GetMapping("/product")
    public String mostrarFormularioNuevoProducto(Model model) {
        model.addAttribute("producto", new Producto());
        return "Management/nuevo-producto";
    }

    @PostMapping("/product/new")
    public String crearNuevoProducto(
            Principal userInSession,
            @ModelAttribute("producto") Producto producto,
            @RequestParam("archivoimagen") MultipartFile imagen,
            BindingResult bindingResult,
            Model model
    ) throws IOException {

        if (!imagen.isEmpty()) {
            byte[] imagenBytes = imagen.getBytes();
            //TODO save image on redis
        }
        servicio.crearProducto(producto, userInSession.getName());
        model.addAttribute("success", "Producto añadido al catalogo con exito");
        return "redirect:/manager/products";
    }

    @GetMapping("/product/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Producto producto = servicio.buscarProductoPorId(id);
        if (producto == null) {
            return "redirect:/manager/products";
        }
        model.addAttribute("producto", producto);
        return "Management/editar-producto";
    }

    @PostMapping("/product/edit/{id}")
    public String editarProducto(
            @PathVariable Long id,
            @ModelAttribute("producto") Producto producto,
            @RequestParam("archivoimagen") MultipartFile imagen
    ) throws IOException {
        if (!imagen.isEmpty()) {
            //byte[] imagenBytes = imagen.getBytes();
            //TODO save image data
        }
        servicio.actualizarProducto(id, producto);
        return "redirect:/manager/products";
    }

}
