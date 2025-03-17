package com.proyecto.PlayApp.Controller.Restaurante;

import com.proyecto.PlayApp.entity.Bebida;
import com.proyecto.PlayApp.entity.Carrito;
import com.proyecto.PlayApp.entity.TipoBebida;
import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.service.BebidaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/bebida")
public class BebidaController {

    @Autowired
    private BebidaService bebidaService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(TipoBebida.class, new PropertyEditorSupport() {
            @Override public void setAsText(String text) {
                setValue(TipoBebida.valueOf(text.toUpperCase()));
            }
        });
    }

    // Mostrar el formulario para registrar una nueva bebida
    @GetMapping("/registro-bebida")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("bebida", new Bebida());
        return "registro-bebida";
    }

    // Método para manejar la vista de administración de bebidas
    @GetMapping("/admin-bebidas")
    public String listarBebidas(HttpSession session, Model model) {
        String nombreRestaurante = (String) session.getAttribute("nombreRestaurante");
        if (nombreRestaurante == null) {
            return "redirect:/login"; // Redirige al login si no hay sesión activa
        }
        List<Bebida> bebidas = bebidaService.obtenerTodasLasBebidas();
        model.addAttribute("nombreRestaurante", nombreRestaurante);
        model.addAttribute("bebidas", bebidas);
        return "Restaurante/admin-bebidas"; // Asegúrate de que este nombre coincida con la vista
    }

    // Registrar una nueva bebida con imagen
    @PostMapping("/registrar")
    public String registrarBebida(@ModelAttribute Bebida bebida,
                                  @RequestParam("archivoimagen") MultipartFile imagen,
                                  HttpSession session, Model model) throws IOException {
        String nombreRestaurante = (String) session.getAttribute("nombreRestaurante");
        if (nombreRestaurante == null) {
            return "redirect:/login"; // Redirige al login si no hay sesión activa
        }
        if (!imagen.isEmpty()) {
            byte[] imagenBytes = imagen.getBytes();
            bebida.setImagen(imagenBytes);
        }
        bebidaService.agregarBebida(bebida);
        model.addAttribute("nombreRestaurante", nombreRestaurante);
        return "redirect:/bebida/admin-bebidas"; // Redirigir a la vista correcta
    }

    // Mostrar el formulario para editar una bebida existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Optional<Bebida> bebida = bebidaService.obtenerBebidaPorId(id);
        if (bebida.isPresent()) {
            model.addAttribute("bebida", bebida.get());
            return "Restaurante/editar-bebidas";
        } else {
            return "redirect:/bebida/admin-bebidas";
        }
    }

    // Obtener una bebida por ID
    @GetMapping("/{id}")
    public String obtenerBebidaPorId(@PathVariable Long id, Model model) {
        Optional<Bebida> bebida = bebidaService.obtenerBebidaPorId(id);
        if (bebida.isPresent()) {
            model.addAttribute("bebida", bebida.get());
            return "detalle-bebida";
        } else {
            return "redirect:/bebida/admin-bebidas";
        }
    }

    // Actualizar una bebida existente
    @PostMapping("/editar/{id}")
    public String actualizarBebida(@PathVariable Long id,
                                   @ModelAttribute Bebida bebida,
                                   @RequestParam("archivoimagen") MultipartFile imagen) throws IOException {
        if (!imagen.isEmpty()) {
            byte[] imagenBytes = imagen.getBytes();
            bebida.setImagen(imagenBytes);
        }
        bebidaService.actualizarBebida(id, bebida);
        return "redirect:/bebida/admin-bebidas";
    }

    // Eliminar una bebida
    @PostMapping("/eliminar/{id}")
    public String eliminarBebida(@PathVariable Long id) {
        bebidaService.eliminarBebida(id);
        return "redirect:/bebida/admin-bebidas";
    }

    @GetMapping
    public String verBebidasPorCategoria(@RequestParam(value = "tipo", required = false) TipoBebida tipo, Model model, HttpSession session) {
        List<Bebida> bebidas;
        if (tipo != null) {
            bebidas = bebidaService.obtenerBebidasPorTipo(tipo);
        } else {
            bebidas = bebidaService.obtenerTodasLasBebidas();
        }
        model.addAttribute("bebidas", bebidas);

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

        return "bebida"; // Carga el archivo bebida.html
    }

}
