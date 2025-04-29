package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.dto.CompraDTO;
import com.proyecto.PlayApp.dto.EnvioPagoDTO;
import com.proyecto.PlayApp.entity.*;
import com.proyecto.PlayApp.repository.PedidoItemRepository;
import com.proyecto.PlayApp.repository.PedidoRepository;
import com.proyecto.PlayApp.repository.ProductoRepository;
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
    private final ProductoRepository productos;
    private final EnvioService envios;
    private final PagoService pagos;

    public Pedido RealizarPedido(CompraDTO compra){
        Usuario usuario = usuarios.findByCorreo(compra.getCorreoUsuario()).orElse(new Usuario());
        if (compra.getCarrito().isEmpty()) throw new IllegalStateException("Carrito vacio");

        double total = compra.getCarrito().stream()
                .mapToDouble(item -> item.getPrecio() * item.getCantidad())
                .sum();
        compra.getEnvioPago().setValor(total);
        Envio envio = realizarOrdenEnvio(compra.getEnvioPago(), usuario);
        Pago pago = realizarPago(compra.getEnvioPago(),usuario);


        Pedido order = Pedido.builder()
                .estado(0)
                .total(total)
                .usuario(usuario)
                .envio(envio)
                .pago(pago)
                .timestamp(LocalDateTime.now())
                .build();
        Pedido pedido = pedidos.save(order);
        guardarDetallesPedido(pedido.getId(), compra.getCarrito());

        return new Pedido();
    }

    private Envio realizarOrdenEnvio(EnvioPagoDTO datos , Usuario usuario){

        Envio envio = Envio.builder()
                .latitud(datos.getLatitud())
                .longitud(datos.getLongitud())
                .mesa(0)
                .carpa(0)
                .dirreccion("ONCONSTRUCT")
                .usuario(usuario)
                .build();
        return envios.crearEnvio(envio);
    }

    private Pago realizarPago(EnvioPagoDTO datos , Usuario usuario){
        Pago pago = Pago.builder()
                .valor(datos.getValor())
                .metodo(datos.getMetodoPago())
                .usuario(usuario)
                .build();
        return pagos.crearPago(pago);
    }


    private void guardarDetallesPedido(Long pedidoId, List<CarritoItem> items){
        items.forEach(carritoItem -> {
            PedidoItem pedido = PedidoItem.builder()
                    .pedidoId(pedidoId)
                    .productoId(Long.valueOf(carritoItem.getProductoId()))
                    .cantidad(carritoItem.getCantidad())
                    .build();

            detalles.save(pedido);
            actualizarCatalogo(Long.valueOf(carritoItem.getProductoId()),carritoItem.getCantidad());
        });
    }

    private void actualizarCatalogo(Long producto, Integer cantidad){
        Producto catalogo =  productos.findById(producto).orElse(new Producto());
        float nuevoStock = (catalogo.getStock() - cantidad);
        catalogo.setStock(nuevoStock);
        productos.save(catalogo);
    }




}
