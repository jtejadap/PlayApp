package com.proyecto.PlayApp.Controller.Restaurante;

import com.proyecto.PlayApp.entity.Carrito;
import com.proyecto.PlayApp.entity.Plato;
import com.proyecto.PlayApp.entity.TipoPlato;
import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.service.PlatoService;
import com.proyecto.PlayApp.service.UploadFileService;
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
@RequestMapping("/comida")
public class PlatoController {

    @Autowired
    private PlatoService platoService;

    @Autowired
    private UploadFileService uploadFileService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(TipoPlato.class, new PropertyEditorSupport() {
            @Override public void setAsText(String text) {
                setValue(TipoPlato.valueOf(text.toUpperCase()));
            }
        });
    }

    // Mostrar el formulario para registrar un nuevo plato
    @GetMapping("/registro-plato")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("plato", new Plato());
        return "registro-plato";
    }

    @GetMapping("/admin-restaurante")
    public String listarPlatos(HttpSession session, Model model) {
        String nombreRestaurante = (String) session.getAttribute("nombreRestaurante");
        if (nombreRestaurante == null) {
            return "redirect:/login"; // Redirige al login si no hay sesión activa
        }
        List<Plato> platos = platoService.obtenerTodosLosPlatos();
        model.addAttribute("nombreRestaurante", nombreRestaurante);
        model.addAttribute("platos", platos);
        return "Restaurante/admin-restaurante";
    }

    // Registrar un nuevo plato con imagen
    @PostMapping("/registrar")
    public String registrarPlato(@ModelAttribute Plato plato,
                                 @RequestParam("archivoimagen") MultipartFile imagen,
                                 HttpSession session, Model model) throws IOException {
        String nombreRestaurante = (String) session.getAttribute("nombreRestaurante");
        if(nombreRestaurante == null){
            return "redirect:/register";
        }
        if (!imagen.isEmpty()) {
            // Convertir el archivo MultipartFile a un arreglo de bytes
            byte[] imagenBytes = imagen.getBytes();

            // Guardar la imagen como byte[] en la base de datos
            plato.setImagen(imagenBytes);
        }

        // Guardar el plato en la base de datos
        platoService.agregarPlato(plato);

        return "redirect:/comida/admin-restaurante";
    }

    // Mostrar el formulario para editar un plato existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Optional<Plato> plato = platoService.obtenerPlatoPorId(id);
        if (plato.isPresent()) {
            model.addAttribute("plato", plato.get());
            return "Restaurante/editar-platos";
        } else {
            return "redirect:/comida/admin-restaurante";
        }
    }

    // Obtener un plato por ID
    @GetMapping("/{id}")
    public String obtenerPlatoPorId(@PathVariable Long id, Model model) {
        Optional<Plato> plato = platoService.obtenerPlatoPorId(id);
        if (plato.isPresent()) {
            model.addAttribute("plato", plato.get());
            return "detalle-plato";
        } else {
            return "redirect:/comida/admin-restaurante";
        }
    }

    // Actualizar un plato existente
    @PostMapping("/editar/{id}")
    public String actualizarPlato(@PathVariable Long id,
                                  @ModelAttribute Plato plato,
                                  @RequestParam("archivoimagen") MultipartFile imagen) throws IOException {
        if (!imagen.isEmpty()) {
            // Convertir el archivo MultipartFile a un arreglo de bytes
            byte[] imagenBytes = imagen.getBytes(); // Actualizar la imagen del plato
            plato.setImagen(imagenBytes);
        }
        // Actualizar el plato en la base de datos
        platoService.actualizarPlato(id, plato);
        return "redirect:/comida/admin-restaurante";
    }

    // Eliminar un plato
    @PostMapping("/eliminar/{id}")
    public String eliminarPlato(@PathVariable Long id) {
        platoService.eliminarPlato(id);
        return "redirect:/comida/admin-restaurante";
    }

    // Mostrar platos por categoría
    @GetMapping
    public String verComidaPorCategoria(@RequestParam(value = "tipoPlato", required = false) TipoPlato tipoPlato, Model model, HttpSession session) {
        List<Plato> platos;
        if (tipoPlato != null) {
            System.out.println("Categoría seleccionada: " + tipoPlato);
            platos = platoService.obtenerPlatosPorTipo(tipoPlato);
        } else {
            System.out.println("No se seleccionó categoría, mostrando todos los platos");
            platos = platoService.obtenerTodosLosPlatos(); // Obteniendo todos los platos
        }
        model.addAttribute("platos", platos);

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            System.out.println("Usuario logueado: " + usuario.getNombre_completo());
            model.addAttribute("nombreUsuario", usuario.getNombre_completo());
        } else {
            System.out.println("No hay usuario logueado.");
            model.addAttribute("nombreUsuario", "Invitado"); // Valor por defecto si no hay usuario logueado
        }
        Carrito carrito = (Carrito) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new Carrito();
            session.setAttribute("carrito", carrito);
        }
        model.addAttribute("carrito", carrito);
        System.out.println("Platos encontrados: " + platos.size());
        return "comida"; // Carga el archivo comida.html
    }
}
