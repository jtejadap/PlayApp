package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.entity.Bebida;
import com.proyecto.PlayApp.entity.Plato;
import com.proyecto.PlayApp.entity.Producto;
import com.proyecto.PlayApp.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
