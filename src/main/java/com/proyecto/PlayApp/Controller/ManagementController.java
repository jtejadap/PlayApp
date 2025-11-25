package com.proyecto.PlayApp.Controller;

import com.proyecto.PlayApp.dto.BusquedaDTO;
import com.proyecto.PlayApp.entity.Pedido;
import com.proyecto.PlayApp.entity.Producto;
import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.service.*;
import com.proyecto.PlayApp.util.PaginationMaker;
import lombok.RequiredArgsConstructor;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagementController {
    private final ProductoService servicio;
    private final PedidoService pedidos;

    @GetMapping("/dashboard")
    public String viewAdminPage(Principal principal, Model model) {
        model.addAttribute("nombreRestaurante", principal.getName());
        return "Management/dashboard";
    }

    @GetMapping("/products")
    public String mostrarProductos(
            Principal session,
            Model model,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "6") Integer size,
            @RequestParam(name = "sort", defaultValue = "[{\"campo\":\"nombre\",\"direccion\":\"asc\"}]") String sort,
            @RequestParam(name = "nombre", required = false) String nombre
    ) {
        Page<Producto> resultado = servicio.listarTodosLosProductos(
                BusquedaDTO.builder()
                        .id(session.getName())
                        .nombre(nombre)
                        .page(page)
                        .size(size)
                        .sort(sort)
                        .build()
        );
        model.addAttribute("nombreRestaurante", session.getName());
        model.addAttribute("productos", resultado);
        model.addAttribute("paginas", new PaginationMaker().makePages(resultado));
        return "Management/productos";
    }

    @GetMapping("/product")
    public String mostrarFormularioNuevoProducto(Model model) {
        model.addAttribute("producto", new Producto());
        return "Management/nuevo-producto";
    }

    @PostMapping("/product/new")
    public String crearNuevoProducto(
            Principal userInSession,
            @ModelAttribute("producto") Producto producto,
            @RequestParam("archivoimagen") MultipartFile imagen,
            BindingResult bindingResult,
            Model model
    ) throws IOException {

        if (!imagen.isEmpty()) {
            producto.setImagen(new Binary(BsonBinarySubType.BINARY, imagen.getBytes()));
        }
        servicio.crearProducto(producto, userInSession.getName());
        model.addAttribute("success", "Producto añadido al catalogo con exito");
        return "redirect:/manager/products";
    }

    @GetMapping("/product/{id}")
    public String mostrarFormularioEdicion(@PathVariable String id, Model model) {
        Producto producto = servicio.buscarProductoPorId(id).orElse(null);
        if (producto == null) {
            return "redirect:/manager/products";
        }
        model.addAttribute("producto", producto);
        return "Management/editar-producto";
    }

    @PostMapping("/product/edit/{id}")
    public String editarProducto(
            @PathVariable String id,
            @ModelAttribute("producto") Producto producto,
            @RequestParam("archivoimagen") MultipartFile imagen
    ) throws IOException {
        if (!imagen.isEmpty()) {
            producto.setImagen(new Binary(BsonBinarySubType.BINARY, imagen.getBytes()));
        }
        servicio.actualizarProducto(id, producto);
        return "redirect:/manager/products";
    }

    @GetMapping("/orders")
    public String mostrarPedidos(
            Principal session,
            Model model,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size,
            @RequestParam(name = "sort", defaultValue = "[{\"campo\":\"timestamp\",\"direccion\":\"desc\"}]") String sort,
            @RequestParam(name = "estado", required = false) Integer estado
    ) {

        Page<Pedido> items = pedidos.buscarPedidoPorRestaurate(
                BusquedaDTO.builder()
                        .id(session.getName())
                        .categoria(estado)
                        .page(page)
                        .size(size)
                        .sort(sort)
                        .build()
        );
        model.addAttribute("paginas", new PaginationMaker().makePages(items));
        model.addAttribute("pedidos", items);
        model.addAttribute("nombreRestaurante", session.getName());
        return "Management/orders";
    }

    @GetMapping("/order")
    public String editarProducto(
            @RequestParam(name = "id", defaultValue = "") String id,
            @RequestParam(name = "estado", defaultValue = "1") Integer estado
    ){

        pedidos.actualizarEstadoPedido(estado, id);
        return "redirect:/manager/orders";
    }

    @GetMapping("/product/delete/{id}")
    public String editarProducto(
            @PathVariable String id
    ) {
        servicio.deleteProducto(id);
        return "redirect:/manager/products";
    }

    @GetMapping("/prediccion")
    public String mostrarVistaPrediccion(Principal principal, Model model) {
        model.addAttribute("nombreRestaurante", principal.getName());
        return "Management/manager-prediccion";
    }
}
