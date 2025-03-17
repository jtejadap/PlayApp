package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.entity.Carrito;
import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.repository.ReviewRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@org.springframework.stereotype.Controller
@RequestMapping("/")
public class Controller {

    @Autowired
    private ReviewRepository reviewRepository;

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

    @GetMapping("/login-register")
    public String showLogin(){
        return "login-register";
    }

    @GetMapping("/register")
    public String showRegisterRest(){return "Restaurante/register";}

    @GetMapping("/plato-form")
    public String showPlatoForm(){return "Restaurante/plato-form";}

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

    @GetMapping
    public String home() {
        return "index";
    }

    @GetMapping("/admin-pedidos")
    public String showAdminPedidos(){
        return "Restaurante/admin-pedidos";
    }

}
