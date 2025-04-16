package com.proyecto.PlayApp.Controller;


import com.proyecto.PlayApp.service.CarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;


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

}
