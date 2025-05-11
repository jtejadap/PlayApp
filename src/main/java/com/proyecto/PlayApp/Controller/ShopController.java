package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.dto.BusquedaDTO;
import com.proyecto.PlayApp.entity.Producto;
import com.proyecto.PlayApp.service.CarritoService;
import com.proyecto.PlayApp.service.ProductoService;
import com.proyecto.PlayApp.service.UsuarioService;
import com.proyecto.PlayApp.util.PaginationMaker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {
    private final ProductoService servicio;
    private final CarritoService carrito;
    private final UsuarioService usuarios;

    @GetMapping
    public String mostrarTienda(
            Principal usuario,
            Model model,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "6") Integer size,
            @RequestParam(name = "sort", defaultValue = "[{\"campo\":\"nombre\",\"direccion\":\"asc\"}]") String sort,
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "categoria", required = false) Integer categoria,
            @RequestParam(name = "status", required = false) String status
    ) {
        Page<Producto> resultado = servicio.buscarProductoConPaginaOrdenFiltro(
                BusquedaDTO.builder()
                        .nombre(nombre)
                        .categoria(categoria)
                        .page(page)
                        .size(size)
                        .sort(sort)
                        .build()
        );
        if (status!= null) {
            model.addAttribute("status", status);
        }
        model.addAttribute("carrito", numeroItemsCarrito(usuario));
        model.addAttribute("productos", resultado);
        model.addAttribute("paginas", new PaginationMaker().makePages(resultado));
        return "tienda";
    }

    private int numeroItemsCarrito(Principal user){
        if(user == null){
            return 0;
        }
        return carrito.listarCarrito(user.getName()).size();
    }

}
