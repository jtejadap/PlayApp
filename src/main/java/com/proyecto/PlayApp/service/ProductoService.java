package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.entity.Producto;
import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.repository.ProductoRepository;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    private final ProductoRepository productos;
    private final UsuarioRepository usuarios;

    public ProductoService(ProductoRepository productos, UsuarioRepository usuarios) {
        this.productos = productos;
        this.usuarios = usuarios;
    }

    public List<Producto> listarTodosLosProductos() {
        return productos.findAll();
    }

    public List<Producto> listarTodosLosProductosPorRestaurante(String userMail) {
        Usuario usuario = usuarios.findByCorreo(userMail).orElse(new Usuario());
        return productos.findByRestaurante_Id(usuario.getId());
    }

    public Producto crearProducto(Producto producto, String userMail) {
        Usuario usuario = usuarios.findByCorreo(userMail).orElse(null);
        producto.setRestaurante(usuario);
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
