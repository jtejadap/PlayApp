package com.proyecto.PlayApp.Controller;


import com.proyecto.PlayApp.entity.Resena;
import com.proyecto.PlayApp.service.CarritoService;
import com.proyecto.PlayApp.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;


@Controller
@RequiredArgsConstructor
public class PublicController {
    private final CarritoService carrito;
    private final UsuarioService usuarios;

    @GetMapping("/")
    public String showHome(Principal usuario, Model model){
        model.addAttribute("carrito", numeroItemsCarrito(usuario));
        String nombreusuario = "";
        if(usuario != null){
        nombreusuario = usuarios.buscarUsuario(usuario.getName()).getNombreCompleto();
    }   
        model.addAttribute("nombreUsuario", nombreusuario);
        return "index";
    }

    private int numeroItemsCarrito(Principal user){
        if(user == null){
            return 0;
        }
        return carrito.listarCarrito(user.getName()).size();
    }

    @GetMapping("/access-denied")
    public String error(Principal principal, Model model) {
        model.addAttribute("link",  usuarios.getHomePath(principal));
        return "errorviews/access-denied";
    }
    @GetMapping("/test")
    public String dashboard() {
        throw new RuntimeException("Excepción controlada");
    }

}
