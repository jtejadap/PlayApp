package com.proyecto.PlayApp.Controller;


import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.service.AuthService;
import com.proyecto.PlayApp.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;


@Controller
public class PublicController {
    private final UsuarioService usuarioService;

    public PublicController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String showLogin(){
        return "login";
    }
    /*
    @GetMapping("/inicio")
    public String home(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("topReviews", reviewRepository.findAll(
                PageRequest.of(0, 3, Sort.by(Sort.Order.desc("valoracion")))).getContent());
        model.addAttribute("nombreUsuario", usuario.getNombre_completo());

        Carrito carrito = (Carrito) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new Carrito();
            session.setAttribute("carrito", carrito);
        }
        model.addAttribute("carrito", carrito);

        return "index"; // Cambia "inicio" por el nombre de la vista principal después del login
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
    */


    @GetMapping("/registro-plato")
    public String showFormAggPlato(){return "Restaurante/registro-plato";}

    @GetMapping("/editar-platos")
    public String showEdit(){
        return "Restaurante/editar-platos";
    }

    @GetMapping("/registro-bebida")
    public String showFormAggBebida(){
        return "Restaurante/registro-bebida";
    }

    @GetMapping("/registro-servicio")
    public String showFormAggServicio(){
        return "Restaurante/registro-servicio";
    }


    @PostMapping("/registro")
    public String registrarUsuario( @ModelAttribute("formulario") Usuario formulario, Model model) {
        try {
            usuarioService.crearUsuario(formulario);
            model.addAttribute("registroExitoso", "Usuario registrado exitosamente. Ahora puedes iniciar sesión.");
            return "login"; // Permanecer en la página de registro para mostrar el mensaje de éxito
        } catch (Exception e) {
            model.addAttribute("error", "Hubo un problema al registrar el usuario. Inténtalo de nuevo.");
            return "login";
        }
    }



}
