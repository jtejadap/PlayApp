package com.proyecto.PlayApp.Controller.Carrito;


import com.proyecto.PlayApp.entity.*;
import com.proyecto.PlayApp.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final AtomicLong itemIdGenerator = new AtomicLong(1);

    @Autowired
    private PlatoService platoService;

    @Autowired
    private BebidaService bebidaService;

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private PedidoService pedidoService;


    @PostMapping("/agregar/plato/{id}")
    public String agregarPlatoAlCarrito(@PathVariable Long id, HttpSession session) {
        Optional<Plato> optionalPlato = platoService.obtenerPlatoPorId(id);
        if (optionalPlato.isPresent()) {
            Plato plato = optionalPlato.get();
            Carrito carrito = (Carrito) session.getAttribute("carrito");
            if (carrito == null) {
                carrito = new Carrito();
                session.setAttribute("carrito", carrito);
            }

            Optional<CarritoItem> itemExistente = carrito.getItems().stream()
                    .filter(item -> item.getPlato() != null && item.getPlato().getId().equals(id))
                    .findFirst();

            if (itemExistente.isPresent()) {
                CarritoItem item = itemExistente.get();
                item.setCantidad(item.getCantidad() + 1);
            } else {
                CarritoItem carritoItem = new CarritoItem();
                carritoItem.setId(itemIdGenerator.getAndIncrement());
                carritoItem.setPlato(plato);
                carritoItem.setCarrito(carrito);
                carritoItem.setCantidad(1);
                carrito.getItems().add(carritoItem);
            }
        }
        return "redirect:/comida";
    }

    @PostMapping("/agregar/bebida/{id}")
    public String agregarBebidaAlCarrito(@PathVariable Long id, HttpSession session) {
        Optional<Bebida> optionalBebida = bebidaService.obtenerBebidaPorId(id);
        if (optionalBebida.isPresent()) {
            Bebida bebida = optionalBebida.get();
            Carrito carrito = (Carrito) session.getAttribute("carrito");
            if (carrito == null) {
                carrito = new Carrito();
                session.setAttribute("carrito", carrito);
            }

            Optional<CarritoItem> itemExistente = carrito.getItems().stream()
                    .filter(item -> item.getBebida() != null && item.getBebida().getId().equals(id))
                    .findFirst();

            if (itemExistente.isPresent()) {
                CarritoItem item = itemExistente.get();
                item.setCantidad(item.getCantidad() + 1);
            } else {
                CarritoItem carritoItem = new CarritoItem();
                carritoItem.setId(itemIdGenerator.getAndIncrement());
                carritoItem.setBebida(bebida);
                carritoItem.setCarrito(carrito);
                carritoItem.setCantidad(1);
                carrito.getItems().add(carritoItem);
            }
        }
        return "redirect:/bebida";
    }

    @PostMapping("/agregar/servicio/{id}")
    public String agregarServicioAlCarrito(@PathVariable Long id, HttpSession session) {
        Optional<Servicio> optionalServicio = servicioService.obtenerServicioPorId(id);
        if (optionalServicio.isPresent()) {
            Servicio servicio = optionalServicio.get();
            Carrito carrito = (Carrito) session.getAttribute("carrito");
            if (carrito == null) {
                carrito = new Carrito();
                session.setAttribute("carrito", carrito);
            }

            Optional<CarritoItem> itemExistente = carrito.getItems().stream()
                    .filter(item -> item.getServicio() != null && item.getServicio().getId().equals(id))
                    .findFirst();

            if (itemExistente.isPresent()) {
                CarritoItem item = itemExistente.get();
                item.setCantidad(item.getCantidad() + 1);
            } else {
                CarritoItem carritoItem = new CarritoItem();
                carritoItem.setId(itemIdGenerator.getAndIncrement());
                carritoItem.setServicio(servicio);
                carritoItem.setCarrito(carrito);
                carritoItem.setCantidad(1);
                carrito.getItems().add(carritoItem);
            }
        }
        return "redirect:/servicio";
    }

    @DeleteMapping("/eliminar/{id}")
    public String eliminarDelCarrito(@PathVariable Long id, HttpSession session) {
        carritoService.eliminarItem(session, id);
        return "redirect:/carrito";
    }

    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        Carrito carrito = (Carrito) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new Carrito();
            session.setAttribute("carrito", carrito);
        }
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null){
            model.addAttribute("nombreUsuario", usuario.getNombre_completo());
        }else{
            model.addAttribute("nombreUsuario", "Invitado");
        }
        model.addAttribute("carrito", carrito);

        double total = carrito.getItems().stream()
                .mapToDouble(item -> item.getCantidad() * (item.getPlato() != null ? item.getPlato().getPrecio() :
                        item.getBebida() != null ? item.getBebida().getPrecio() :
                                item.getServicio().getPrecio()))
                .sum();
        model.addAttribute("totalCarrito", total);

        return "carrito";
    }

    @PostMapping("/realizar-compra")
    public String realizarCompra(HttpSession session, Model model) {
        try {
            Pedido pedido = pedidoService.crearPedido(session);
            model.addAttribute("pedido", pedido);
            return "pedido-confirmacion"; // Carga la vista de confirmación del pedido
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/carrito"; // Vuelve al carrito en caso de error
        }
    }
}

