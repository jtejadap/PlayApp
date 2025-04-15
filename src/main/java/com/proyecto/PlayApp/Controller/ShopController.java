package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.dto.BusquedaDTO;
import com.proyecto.PlayApp.entity.Producto;
import com.proyecto.PlayApp.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {
    private final ProductoService servicio;

    @GetMapping
    public String mostrarTienda(Model model){
        List<Producto>  productos = servicio.listarTodosLosProductos();
        model.addAttribute("productos", productos);
        return "bebida";
    }

    @GetMapping("/search")
    public String test(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "6") Integer size,
            @RequestParam(name = "sort", defaultValue = "[{\"field\":\"nombre\",\"direction\":\"desc\"}]") String sort,
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "precio", required = false) Float precio,
            @RequestParam(name = "stock", required = false) Float stock,
            @RequestParam(name = "categoria", required = false) Integer categoria
    ) {
        Page<Producto> resultado = servicio.searchEmployeeWithPaginationSortingAndFiltering(
                BusquedaDTO.builder()
                        .nombre(nombre)
                        .precio(precio)
                        .stock(stock)
                        .categoria(categoria)
                        .page(page)
                        .size(size)
                        .sort(sort)
                        .build());

        model.addAttribute("productos", resultado);
        return "bebida";
    }

}
