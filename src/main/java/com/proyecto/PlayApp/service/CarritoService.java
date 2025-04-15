package com.proyecto.PlayApp.service;

import com.proyecto.PlayApp.dto.ItemDTO;
import com.proyecto.PlayApp.entity.Item;
import com.proyecto.PlayApp.entity.Producto;
import com.proyecto.PlayApp.entity.Usuario;
import com.proyecto.PlayApp.repository.ProductoRepository;
import com.proyecto.PlayApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarritoService {
    private static final String PREFIJO = "cart:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final UsuarioRepository usuarios;
    private final ProductoRepository productos;

    public void agregar(ItemDTO campos) {
        Usuario usuarioInstancia = usuarios.findByCorreo(campos.getCorreo()).orElse(new Usuario());
        String key = PREFIJO + usuarioInstancia.getId();
        Producto producto = productos.findById(campos.getItemId()).orElse(new Producto());
        Item item = Item.builder()
                .productoId(Long.toString(producto.getId()))
                .nombre(producto.getNombre())
                .cantidad(campos.getCantidad())
                .precio(producto.getPrecio())
                .build();

        HashOperations<String, String, Item> hash = redisTemplate.opsForHash();

        hash.put(key, item.getProductoId(), item);
    }

    public List<Item> listarCarrito(String usuario) {
        Usuario usuarioInstancia = usuarios.findByCorreo(usuario).orElse(new Usuario());
        String key = PREFIJO + usuarioInstancia.getId();
        HashOperations<String, String, Item> hashOps = redisTemplate.opsForHash();
        return new ArrayList<>(hashOps.values(key));
    }

    public void eliminar(String usuario, String producto) {
        String key = PREFIJO + usuario;
        redisTemplate.opsForHash().delete(key, producto);
    }

    public void limpiar(String usuario) {
        String key = PREFIJO + usuario;
        redisTemplate.delete(key);
    }
}
