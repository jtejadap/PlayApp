package com.proyecto.PlayApp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.PlayApp.dto.*;
import com.proyecto.PlayApp.entity.*;
import com.proyecto.PlayApp.repository.PedidoRepository;
import com.proyecto.PlayApp.repository.ProductoRepository;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import com.proyecto.PlayApp.repository.specification.PedidoSpecification;
import com.proyecto.PlayApp.repository.specification.ProductoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidos;
    private final UsuarioRepository usuarios;
    private final ProductoRepository productos;
    private final MongoTemplate mongoTemplate;

    public Pedido RealizarPedido(CompraDTO compra){
        Usuario usuario = usuarios.findUsuarioByCorreo(compra.getCorreoUsuario());
        if (compra.getCarrito().isEmpty()) throw new IllegalStateException("Carrito vacio");

        double total = compra.getCarrito().stream()
                .mapToDouble(item -> item.getPrecio() * item.getCantidad())
                .sum();
        compra.getEnvioPago().setValor(total);
        Envio envio = crearEnvio(compra.getEnvioPago(), usuario);
        Pago pago = crearPago(compra.getEnvioPago(),usuario);


        Pedido order = Pedido.builder()
                .estado(0)
                .total(total)
                .cliente(usuario)
                .envio(envio)
                .pago(pago)
                .timestamp(LocalDateTime.now())
                .build();

        order.setCarrito(crearCarrito(compra.getCarrito()));
        return pedidos.save(order);
    }

    private Envio crearEnvio(EnvioPagoDTO datos , Usuario usuario){
        return Envio.builder()
                .latitud(datos.getLatitud())
                .longitud(datos.getLongitud())
                .dirreccion(datos.getDireccion())
                .descripcion(datos.getDescripcion())
                .carpa(datos.getMesa())
                .mesa(datos.getMesa())
                .fecha(LocalDateTime.now())
                .clienteId(usuario.getId())
                .build();
    }

    private Pago crearPago(EnvioPagoDTO datos , Usuario usuario){
        return Pago.builder()
                .valor(datos.getValor())
                .metodo(datos.getMetodoPago())
                .estado(0)
                .clienteId(usuario.getId())
                .fecha(LocalDateTime.now())
                .build();
    }


    private List<PedidoItem> crearCarrito(List<CarritoItem> items){
        List<PedidoItem> carrito = new ArrayList<>();
        items.forEach(carritoItem -> {
            Producto producto =  productos.findById(carritoItem.getProductoId()).orElse(new Producto());

            PedidoItem item = PedidoItem.builder()
                    .producto(producto)
                    .subtotal(carritoItem.getPrecio() * carritoItem.getCantidad())
                    .cantidad(carritoItem.getCantidad())
                    .build();

            carrito.add(item);
            actualizarCatalogo(producto,carritoItem.getCantidad());
        });
        return carrito;
    }

    private void actualizarCatalogo(Producto producto, Integer cantidad){
        double nuevoStock = (producto.getStock() - cantidad);
        producto.setStock(nuevoStock);
        productos.save(producto);
    }

    public Page<Pedido> buscarPedidoConPaginaOrdenFiltro(BusquedaDTO busqueda) {
        // Creación y conversion de orden de registros
        List<OrdenDTO> ordenes = jsonStringToOrdenDTO(busqueda.getSort());
        List<Sort.Order> ordenado = construirOrden(ordenes);


        // Creación de solicitud de orden
        PageRequest solicitudPagina = PageRequest.of(
                busqueda.getPage(),
                busqueda.getSize(),
                Sort.by(ordenado)
        );

        Query query = new Query();
        Usuario usuario = usuarios.findById(busqueda.getId()).orElse(new Usuario());
        query.addCriteria(Criteria.where("cliente").is(usuario));
        if(busqueda.getCategoria() != null) {
            query.addCriteria(Criteria.where("estado").is(busqueda.getCategoria()));
        }
        query.with(solicitudPagina);

        // Retornar registros de acuerdo especificación y paginación
        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Pedido.class),
                solicitudPagina,
                () -> mongoTemplate.count(query.skip(0).limit(0), Pedido.class)
        );
    }

    private List<OrdenDTO> jsonStringToOrdenDTO(String jsonString) {
        try {
            ObjectMapper obj = new ObjectMapper();
            return obj.readValue(jsonString, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }

    private List<Sort.Order> construirOrden(List<OrdenDTO> ordenes){
        List<Sort.Order> ordenado = new ArrayList<>();
        if (ordenes != null) {
            for(OrdenDTO sort: ordenes) {
                Sort.Direction direction = Objects.equals(sort.getDireccion(), "desc")
                        ? Sort.Direction.DESC : Sort.Direction.ASC;
                ordenado.add(new Sort.Order(direction,sort.getCampo()));
            }
        }
        return ordenado;
    }

    public Pedido actualizarEstadoPagoPedido(Integer estado, String id){
        Pedido pedido = pedidos.findById(id).orElseThrow(() -> new IllegalStateException("Pedido no encontrado"));
        pedido.getPago().setEstado(estado);
        return pedidos.save(pedido);
    }




}
