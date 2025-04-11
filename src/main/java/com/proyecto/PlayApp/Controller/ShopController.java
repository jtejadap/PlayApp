package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.entity.Producto;
import com.proyecto.PlayApp.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/shop")
public class ShopController {
    private final ProductoService servicio;

    public ShopController(ProductoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public String mostrarTienda(Model model){
        List<Producto>  productos = servicio.listarTodosLosProductos();
        model.addAttribute("productos", productos);
        return "bebida";
    }

}
