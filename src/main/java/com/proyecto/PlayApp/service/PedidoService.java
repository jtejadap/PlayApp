package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.entity.CarritoItem;
import com.proyecto.PlayApp.entity.Pedido;
import com.proyecto.PlayApp.entity.PedidoItem;
import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.repository.PedidoItemRepository;
import com.proyecto.PlayApp.repository.PedidoRepository;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidos;
    private final PedidoItemRepository detalles;
    private final UsuarioRepository usuarios;

    public Pedido crearPedido(String usermail, List<CarritoItem> items) {
        Usuario usuario = usuarios.findByCorreo(usermail).orElse(new Usuario());
        if (items.isEmpty()) throw new IllegalStateException("Carrito vacio");

        double total = items.stream()
                .mapToDouble(item -> item.getPrecio() * item.getCantidad())
                .sum();


        Pedido order = Pedido.builder()
                .usuarioId(usuario.getId())
                .estado(0)
                .total(total)
                .timestamp(LocalDateTime.now())
                .build();
        Pedido pedido = pedidos.save(order);
        guardarDetallesPedido(pedido.getId(), items);
        return pedido;
    }

    private void guardarDetallesPedido(Long pedidoId, List<CarritoItem> items){
        items.forEach(carritoItem -> {
            PedidoItem pedido = PedidoItem.builder()
                    .pedidoId(pedidoId)
                    .productoId(Long.valueOf(carritoItem.getProductoId()))
                    .cantidad(carritoItem.getCantidad())
                    .build();

            detalles.save(pedido);
        });
    }
}
