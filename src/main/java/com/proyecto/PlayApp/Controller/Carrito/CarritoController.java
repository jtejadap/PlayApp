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
    private BebidaService bebidaService;


    @Autowired
    private CarritoService carritoService;

    @Autowired
    private PedidoService pedidoService;




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

    @DeleteMapping("/eliminar/{id}")
    public String eliminarDelCarrito(@PathVariable Long id, HttpSession session) {
        carritoService.eliminarItem(session, id);
        return "redirect:/carrito";
    }



    @PostMapping("/realizar-compra")
    public String realizarCompra(HttpSession session, Model model) {
        try {
            Pedido pedido = new Pedido();
            model.addAttribute("pedido", pedido);
            return "pedido-confirmacion"; // Carga la vista de confirmación del pedido
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/carrito"; // Vuelve al carrito en caso de error
        }
    }
}

