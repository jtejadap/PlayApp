package com.proyecto.PlayApp.Controller;


import com.proyecto.PlayApp.entity.Review;
import com.proyecto.PlayApp.service.CarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;


@Controller
@RequiredArgsConstructor
public class PublicController {
    private final CarritoService carrito;

    @GetMapping("/")
    public String showHome(Principal usuario, Model model){
        model.addAttribute("carrito", numeroItemsCarrito(usuario));
        return "index";
    }

    private int numeroItemsCarrito(Principal user){
        if(user == null){
            return 0;
        }
        return carrito.listarCarrito(user.getName()).size();
    }

    @GetMapping("/contacto")
    public String showContacto(Principal session, Model model) {
        // Recupera las últimas 2 reseñas ordenadas por ID de forma descendente
        model.addAttribute("reviews", new ArrayList<Review>());
        model.addAttribute("carrito", numeroItemsCarrito(session));
        return "contacto"; // Carga el archivo contacto.html
    }

}
