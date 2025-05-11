package com.proyecto.PlayApp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.PlayApp.dto.*;
import com.proyecto.PlayApp.entity.*;
import com.proyecto.PlayApp.repository.PedidoItemRepository;
import com.proyecto.PlayApp.repository.PedidoRepository;
import com.proyecto.PlayApp.repository.ProductoRepository;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import com.proyecto.PlayApp.repository.specification.PedidoSpecification;
import com.proyecto.PlayApp.repository.specification.ProductoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        Usuario usuario = usuarios.findUsuarioByCorreo(compra.getCorreoUsuario());
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
                //.usuario(usuario)
                .envio(envio)
                .pago(pago)
                .timestamp(LocalDateTime.now())
                .build();
        Pedido pedido = pedidos.save(order);
        guardarDetallesPedido(pedido, compra.getCarrito());
        return pedido;
    }

    private Envio realizarOrdenEnvio(EnvioPagoDTO datos , Usuario usuario){

        Envio envio = Envio.builder()
                .latitud(datos.getLatitud())
                .longitud(datos.getLongitud())
                .mesa(datos.getMesa())
                .carpa(datos.getMesa())
                .dirreccion(datos.getDireccion())
                .descripcion(datos.getDescripcion())
                //.usuario(usuario)
                .build();
        return envios.crearEnvio(envio);
    }

    private Pago realizarPago(EnvioPagoDTO datos , Usuario usuario){
        Pago pago = Pago.builder()
                .estado(0)
                .valor(datos.getValor())
                .metodo(datos.getMetodoPago())
                //.usuario(usuario)
                .build();
        return pagos.crearPago(pago);
    }


    private void guardarDetallesPedido(Pedido pedido, List<CarritoItem> items){
        items.forEach(carritoItem -> {
            //Producto producto =  productos.findById(Long.valueOf(carritoItem.getProductoId())).orElse(new Producto());

            PedidoItem item = PedidoItem.builder()
                    //.producto(producto)
                    .pedido(pedido)
                    .cantidad(carritoItem.getCantidad())
                    .build();

            detalles.save(item);
            //actualizarCatalogo(producto,carritoItem.getCantidad());
        });
    }
    /*
    private void actualizarCatalogo(Producto producto, Integer cantidad){
        float nuevoStock = (producto.getStock() - cantidad);
        producto.setStock(nuevoStock);
        productos.save(producto);
    }*/

    public List<Pedido> listarPedidosPorUsuario(String mail){
        Usuario usuario = usuarios.findUsuarioByCorreo(mail);
        //return pedidos.findByUsuario_id(usuario.getId());
        return new ArrayList<>();
    }

    public Page<Pedido> buscarPedidoConPaginaOrdenFiltro(BusquedaDTO busqueda) {
        // Creación de filtrosDTO
        FiltrosDTO filtros = FiltrosDTO.builder()
                .nombre(busqueda.getNombre())
                .id(busqueda.getId())
                .categoria(busqueda.getCategoria())
                .build();

        // Creación y conversion de orden de registros
        List<OrdenDTO> ordenes = jsonStringToOrdenDTO(busqueda.getSort());
        List<Sort.Order> ordenado = construirOrden(ordenes);


        // Creación de solicitud de orden
        PageRequest solicitudPagina = PageRequest.of(
                busqueda.getPage(),
                busqueda.getSize(),
                Sort.by(ordenado)
        );

        // Crear especificación (filtros de busqueda)
        Specification<Pedido> specification = PedidoSpecification.getSpecification(filtros);

        // Retornar registros de acuerdo especificación y paginación
        return pedidos.findAll(specification,solicitudPagina);
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




}
