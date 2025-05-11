package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.dto.BusquedaDTO;
import com.proyecto.PlayApp.dto.FiltrosDTO;
import com.proyecto.PlayApp.dto.OrdenDTO;
import com.proyecto.PlayApp.entity.Producto;
import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.repository.ProductoRepository;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import com.proyecto.PlayApp.repository.specification.ProductoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository productos;
    private final UsuarioRepository usuarios;

    public List<Producto> listarTodosLosProductos() {
        return productos.findAll();
    }

    public List<Producto> listarTodosLosProductosPorRestaurante(String userMail) {
        Usuario usuario = usuarios.findUsuarioByCorreo(userMail);
       // return productos.findByRestaurante_Id(usuario.getId());
        return new ArrayList<>();
    }

    public Producto crearProducto(Producto producto, String userMail) {
        Usuario usuario = usuarios.findUsuarioByCorreo(userMail);
        producto.setEntidad(usuario);
        return productos.save(producto);
    }

    public Optional<Producto> buscarProductoPorId(String id) {
       // Optional<Producto> producto = productos.findById(id);
       // return producto.orElse(null);
        return productos.findById(id);
    }

    public Producto actualizarProducto(String id, Producto formulario) {

        Optional<Producto> registros = productos.findById(id);
        if(registros.isEmpty()){
            return null;
        }

        Producto producto = registros.get();

        producto.setNombre(formulario.getNombre());
        producto.setPrecio(formulario.getPrecio());
        producto.setStock(formulario.getStock());
        producto.setDescripcion(formulario.getDescripcion());
        producto.setTipo(formulario.getTipo());
        producto.setCategoria(formulario.getCategoria());
        producto.setImagen(formulario.getImagen());

        return productos.save(producto);
    }

    public List<Producto>buscarProductoConPaginaOrdenFiltro(BusquedaDTO busqueda) {
        // Creación de filtrosDTO
        FiltrosDTO filtros = FiltrosDTO.builder()
                .nombre(busqueda.getNombre())
                .precio(busqueda.getPrecio())
                .stock(busqueda.getStock())
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
        Specification<Producto> specification = ProductoSpecification.getSpecification(filtros);

        // Retornar registros de acuerdo especificación y paginación
        return new ArrayList<>() ;
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
