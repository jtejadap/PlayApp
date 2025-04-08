package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.entity.*;
import com.proyecto.PlayApp.repository.PedidoRepository;
import com.proyecto.PlayApp.repository.PedidoItemRepository;
import com.proyecto.PlayApp.repository.BebidaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoItemRepository pedidoItemRepository;

    @Autowired
    private BebidaRepository bebidaRepository;

    @Autowired
    private EmailService emailService;

    public Pedido obtenerPedido(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado.");
        }

        Pedido pedido = pedidoRepository.findTopByUsuarioOrderByFechaCreacionDesc(usuario);

        if (pedido == null) {
            throw new RuntimeException("No hay pedidos para este usuario.");
        }

        // Asegurarse de que la lista de items esté inicializada
        if (pedido.getItems() == null) {
            pedido.setItems(new ArrayList<>());
        }

        return pedido;
    }





    private void enviarCorreoDetallesPedido(Usuario usuario, Pedido pedido) {
        String subject = "Nuevo Pedido Realizado";
        StringBuilder body = new StringBuilder();
        body.append("Se ha realizado un nuevo pedido.\n\n");
        body.append("Detalles del Pedido:\n");
        body.append("Usuario: ").append(usuario.getNombre_completo()).append("\n");
        body.append("Fecha de Creación: ").append(pedido.getFechaCreacion()).append("\n");
        body.append("Total: ").append(pedido.getTotal()).append("\n\n");

        body.append("Items:\n");
        /*
        for (PedidoItem item : pedido.getItems()) {
            body.append("- Producto: ").append(item.getPlato() != null ? item.getPlato().getNombre() :
                            item.getBebida() != null ? item.getBebida().getNombre() :
                                    item.getServicio() != null ? item.getServicio().getNombre() : "Desconocido")
                    .append(", Cantidad: ").append(item.getCantidad())
                    .append(", Precio Unitario: ").append(item.getPrecio() / item.getCantidad())
                    .append(", Total: ").append(item.getPrecio())
                    .append("\n");
        }*/

        emailService.enviarCorreo("restaurante@example.com", subject, body.toString());
    }

    public List<Pedido> obtenerTodosLosPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        for (Pedido pedido : pedidos) {
            if (pedido.getItems() == null) {
                pedido.setItems(new ArrayList<>());
            }
        }
        return pedidos;
    }

    public void eliminarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }
}
