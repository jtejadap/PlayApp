package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.dto.BusquedaDTO;
import com.proyecto.PlayApp.entity.Pedido;
import com.proyecto.PlayApp.entity.Producto;
import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.service.CarritoService;
import com.proyecto.PlayApp.service.PedidoService;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UsuarioController {
    private final PedidoService pedidos;
    private final UsuarioService usuarios;
    private final CarritoService carrito;

    @GetMapping("/orders")
    public String mostrarMisPedidos(
            Principal usuario,
            Model model
    ){
        Usuario loggedUser = usuarios.buscarUsuario(usuario.getName());
        Page<Pedido> items = pedidos.buscarPedidoConPaginaOrdenFiltro(
                BusquedaDTO.builder().page(0).size(5)
                        .id(loggedUser.getId())
                        .sort("[{\"campo\":\"timestamp\",\"direccion\":\"desc\"}]")
                        .build()
        );

        model.addAttribute("carrito", numeroItemsCarrito(usuario));
        model.addAttribute("paginas", new PaginationMaker().makePages(items));
        model.addAttribute("pedidos", items);

        String nombreusuario = "";
        if(usuario != null){
        nombreusuario = usuarios.buscarUsuario(usuario.getName()).getNombreCompleto();
        }

        model.addAttribute("nombreUsuario", nombreusuario);
        return "user-orders";
    }

    @GetMapping("/search")
    public String buscarProducto(
            Principal usuario,
            Model model,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size,
            @RequestParam(name = "sort", defaultValue = "[{\"campo\":\"timestamp\",\"direccion\":\"desc\"}]") String sort,
            @RequestParam(name = "estado", required = false) Integer estado
    ) {
        Usuario loggedUser = usuarios.buscarUsuario(usuario.getName());
        Page<Pedido> items = pedidos.buscarPedidoConPaginaOrdenFiltro(
                BusquedaDTO.builder()
                        .id(loggedUser.getId())
                        .categoria(estado)
                        .page(page)
                        .size(size)
                        .sort(sort)
                        .build()
        );
        model.addAttribute("carrito", numeroItemsCarrito(usuario));
        model.addAttribute("paginas", new PaginationMaker().makePages(items));
        model.addAttribute("pedidos", items);
        return "user-orders";
    }

    private int numeroItemsCarrito(Principal user){
        if(user == null){
            return 0;
        }
        return carrito.listarCarrito(user.getName()).size();
    }
}
