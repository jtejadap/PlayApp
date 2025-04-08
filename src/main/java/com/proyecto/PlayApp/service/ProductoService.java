package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.entity.Producto;
import com.proyecto.PlayApp.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    private final ProductoRepository productos;

    public ProductoService(ProductoRepository productos) {
        this.productos = productos;
    }
    public List<Producto> listarTodoslosProductos() {
        return productos.findAll();
    }

    public Producto crearProducto(Producto producto) {
        return productos.save(producto);
    }

    public Producto buscarProductoPorId(Long id) {
        Optional<Producto> producto = productos.findById(id);
        return producto.orElse(null);
    }

    public Producto actualizarProducto(Long id, Producto formulario) {
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

        return productos.save(producto);
    }
}
