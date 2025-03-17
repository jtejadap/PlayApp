package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.entity.*;
import com.proyecto.PlayApp.repository.PedidoRepository;
import com.proyecto.PlayApp.repository.PedidoItemRepository;
import com.proyecto.PlayApp.repository.PlatoRepository;
import com.proyecto.PlayApp.repository.BebidaRepository;
import com.proyecto.PlayApp.repository.ServicioRepository;
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
    private PlatoRepository platoRepository;

    @Autowired
    private BebidaRepository bebidaRepository;

    @Autowired
    private ServicioRepository servicioRepository;

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

    public Pedido crearPedido(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        Carrito carrito = (Carrito) session.getAttribute("carrito");

        if (usuario == null || carrito == null || carrito.getItems().isEmpty()) {
            throw new RuntimeException("No se puede crear un pedido sin usuario o carrito.");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido.setTotal(carrito.getItems().stream()
                .mapToDouble(item -> item.getCantidad() *
                        (item.getPlato() != null ? item.getPlato().getPrecio() :
                                item.getBebida() != null ? item.getBebida().getPrecio() :
                                        item.getServicio() != null ? item.getServicio().getPrecio() : 0))
                .sum());

        List<PedidoItem> pedidoItems = new ArrayList<>();
        for (CarritoItem item : carrito.getItems()) {
            PedidoItem pedidoItem = new PedidoItem();
            pedidoItem.setPedido(pedido);
            if (item.getPlato() != null) {
                pedidoItem.setPlato(item.getPlato());
                actualizarStock(item.getPlato(), item.getCantidad());
            }
            if (item.getBebida() != null) {
                pedidoItem.setBebida(item.getBebida());
                actualizarStock(item.getBebida(), item.getCantidad());
            }
            if (item.getServicio() != null) {
                pedidoItem.setServicio(item.getServicio());
                actualizarStock(item.getServicio(), item.getCantidad());
            }
            pedidoItem.setCantidad(item.getCantidad());
            pedidoItem.setPrecio(item.getCantidad() *
                    (item.getPlato() != null ? item.getPlato().getPrecio() :
                            item.getBebida() != null ? item.getBebida().getPrecio() :
                                    item.getServicio() != null ? item.getServicio().getPrecio() : 0));
            pedidoItems.add(pedidoItem);
        }
        pedido.setItems(pedidoItems);

        pedido = pedidoRepository.save(pedido);
        session.removeAttribute("carrito");

        // Enviar correo al restaurante con los detalles del pedido
        enviarCorreoDetallesPedido(usuario, pedido);

        return pedido;
    }

    private void actualizarStock(Object producto, Integer cantidadComprada) {
        if (producto instanceof Plato) {
            Plato plato = (Plato) producto;
            System.out.println("Actualizando stock para plato: " + plato.getNombre() + ". Cantidad comprada: " + cantidadComprada);
            plato.setCantidad(plato.getCantidad() - cantidadComprada);
            platoRepository.save(plato);
        } else if (producto instanceof Bebida) {
            Bebida bebida = (Bebida) producto;
            System.out.println("Actualizando stock para bebida: " + bebida.getNombre() + ". Cantidad comprada: " + cantidadComprada);
            bebida.setStock(bebida.getStock() - cantidadComprada);
            bebidaRepository.save(bebida);
        }
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
        for (PedidoItem item : pedido.getItems()) {
            body.append("- Producto: ").append(item.getPlato() != null ? item.getPlato().getNombre() :
                            item.getBebida() != null ? item.getBebida().getNombre() :
                                    item.getServicio() != null ? item.getServicio().getNombre() : "Desconocido")
                    .append(", Cantidad: ").append(item.getCantidad())
                    .append(", Precio Unitario: ").append(item.getPrecio() / item.getCantidad())
                    .append(", Total: ").append(item.getPrecio())
                    .append("\n");
        }

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
