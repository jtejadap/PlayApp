package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.entity.Bebida;
import com.proyecto.PlayApp.entity.Carrito;
import com.proyecto.PlayApp.entity.TipoBebida;
import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.service.BebidaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/shop")
public class ShopController {
    private final BebidaService bebidaService;

    public ShopController(BebidaService bebidaService) {
        this.bebidaService = bebidaService;
    }

    @GetMapping("/products")
    public String showAllProducts(Model model){
        List<Bebida>  bebidas = bebidaService.obtenerTodasLasBebidas();
        model.addAttribute("bebidas", bebidas);
        return "bebida";
    }

}
