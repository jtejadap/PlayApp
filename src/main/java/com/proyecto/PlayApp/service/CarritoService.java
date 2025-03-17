package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.entity.*;
import com.proyecto.PlayApp.repository.CarritoRepository;
import com.proyecto.PlayApp.repository.PlatoRepository;
import com.proyecto.PlayApp.repository.BebidaRepository;
import com.proyecto.PlayApp.repository.ServicioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private PlatoRepository platoRepository;

    @Autowired
    private BebidaRepository bebidaRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    public Carrito obtenerCarrito(HttpSession session) {
        Long carritoId = (Long) session.getAttribute("carritoId");
        if (carritoId != null) {
            Optional<Carrito> carrito = carritoRepository.findById(carritoId);
            if (carrito.isPresent()) {
                return carrito.get();
            }
        }
        Carrito nuevoCarrito = new Carrito();
        session.setAttribute("carrito", nuevoCarrito);
        return nuevoCarrito;
    }

    public void agregarPlato(HttpSession session, Long platoId, int cantidad) {
        Carrito carrito = obtenerCarrito(session);
        Optional<Plato> platoOpt = platoRepository.findById(platoId);
        if (platoOpt.isPresent()) {
            Plato plato = platoOpt.get();
            CarritoItem carritoItem = new CarritoItem();
            carritoItem.setCarrito(carrito);
            carritoItem.setPlato(plato);
            carritoItem.setCantidad(cantidad);
            carrito.getItems().add(carritoItem);
            carritoRepository.save(carrito);
        }
    }

    public void agregarBebida(HttpSession session, Long bebidaId, int cantidad) {
        Carrito carrito = obtenerCarrito(session);
        Optional<Bebida> bebidaOpt = bebidaRepository.findById(bebidaId);
        if (bebidaOpt.isPresent()) {
            Bebida bebida = bebidaOpt.get();
            CarritoItem carritoItem = new CarritoItem();
            carritoItem.setCarrito(carrito);
            carritoItem.setBebida(bebida);
            carritoItem.setCantidad(cantidad);
            carrito.getItems().add(carritoItem);
            carritoRepository.save(carrito);
        }
    }

    public void agregarServicio(HttpSession session, Long servicioId, int cantidad) {
        Carrito carrito = obtenerCarrito(session);
        Optional<Servicio> servicioOpt = servicioRepository.findById(servicioId);
        if (servicioOpt.isPresent()) {
            Servicio servicio = servicioOpt.get();
            CarritoItem carritoItem = new CarritoItem();
            carritoItem.setCarrito(carrito);
            carritoItem.setServicio(servicio);
            carritoItem.setCantidad(cantidad);
            carrito.getItems().add(carritoItem);
            carritoRepository.save(carrito);
        }
    }

    public void eliminarItem(HttpSession session, Long itemId) {
        Carrito carrito = (Carrito) session.getAttribute("carrito");
        if (carrito == null) {
            throw new RuntimeException("No se ha encontrado un carrito en la sesión.");
        }

        boolean itemEliminado = carrito.getItems().removeIf(item ->
                item.getId() != null && item.getId().equals(itemId)
        );

        if (!itemEliminado) {
            throw new RuntimeException("No se encontró el ítem con ID: " + itemId);
        }

        session.setAttribute("carrito", carrito);
    }
}


