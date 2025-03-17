package com.proyecto.PlayApp.Controller.Restaurante;

import com.proyecto.PlayApp.entity.Carrito;
import com.proyecto.PlayApp.entity.Servicio;
import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.service.ServicioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/servicio")
public class ServiciosController {

    @Autowired
    private ServicioService servicioService;

    // Mostrar el formulario para registrar un nuevo servicio
    @GetMapping("/registro-servicio")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("servicio", new Servicio());
        return "registro-servicio";
    }

    // Método para manejar la vista de administración de servicios
    @GetMapping("/admin-servicios")
    public String listarServicios(HttpSession session, Model model) {
        String nombreRestaurante = (String) session.getAttribute("nombreRestaurante");
        if (nombreRestaurante == null) {
            return "redirect:/login"; // Redirige al login si no hay sesión activa
        }
        List<Servicio> servicios = servicioService.obtenerTodosLosServicios();
        model.addAttribute("nombreRestaurante", nombreRestaurante);
        model.addAttribute("servicios", servicios);
        return "Restaurante/admin-servicios"; // Asegúrate de que este nombre coincida con la vista
    }

    // Registrar un nuevo servicio con imagen
    @PostMapping("/registrar")
    public String registrarServicio(@ModelAttribute Servicio servicio,
                                    @RequestParam("archivoimagen") MultipartFile imagen,
                                    HttpSession session, Model model) throws IOException {
        String nombreRestaurante = (String) session.getAttribute("nombreRestaurante");
        if (nombreRestaurante == null) {
            return "redirect:/login"; // Redirige al login si no hay sesión activa
        }
        if (!imagen.isEmpty()) {
            byte[] imagenBytes = imagen.getBytes();
            servicio.setImagen(imagenBytes);
        }
        servicioService.agregarServicio(servicio);
        model.addAttribute("nombreRestaurante", nombreRestaurante);
        return "redirect:/servicio/admin-servicios"; // Redirigir a la vista correcta
    }

    // Mostrar el formulario para editar un servicio existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Optional<Servicio> servicio = servicioService.obtenerServicioPorId(id);
        if (servicio.isPresent()) {
            model.addAttribute("servicio", servicio.get());
            return "Restaurante/editar-servicios";
        } else {
            return "redirect:/servicio/admin-servicios";
        }
    }

    // Obtener un servicio por ID
    @GetMapping("/{id}")
    public String obtenerServicioPorId(@PathVariable Long id, Model model) {
        Optional<Servicio> servicio = servicioService.obtenerServicioPorId(id);
        if (servicio.isPresent()) {
            model.addAttribute("servicio", servicio.get());
            return "detalle-servicio";
        } else {
            return "redirect:/servicio/admin-servicios";
        }
    }

    // Actualizar un servicio existente
    @PostMapping("/editar/{id}")
    public String actualizarServicio(@PathVariable Long id,
                                     @ModelAttribute Servicio servicio,
                                     @RequestParam("archivoimagen") MultipartFile imagen) throws IOException {
        if (!imagen.isEmpty()) {
            byte[] imagenBytes = imagen.getBytes();
            servicio.setImagen(imagenBytes);
        }
        servicioService.actualizarServicio(id, servicio);
        return "redirect:/servicio/admin-servicios";
    }

    // Eliminar un servicio
    @PostMapping("/eliminar/{id}")
    public String eliminarServicio(@PathVariable Long id) {
        servicioService.eliminarServicio(id);
        return "redirect:/servicio/admin-servicios";
    }

    @GetMapping
    public String verServicios(Model model, HttpSession session) {
        List<Servicio> servicios = servicioService.obtenerTodosLosServicios();
        model.addAttribute("servicios", servicios);

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            model.addAttribute("nombreUsuario", usuario.getNombre_completo());
        } else {
            model.addAttribute("nombreUsuario", "Invitado");
        }

        Carrito carrito = (Carrito) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new Carrito();
            session.setAttribute("carrito", carrito);
        }
        model.addAttribute("carrito", carrito);

        return "servicio"; // Carga el archivo servicio.html
    }

}
