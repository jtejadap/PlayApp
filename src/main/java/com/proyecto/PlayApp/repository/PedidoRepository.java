package com.proyecto.PlayApp.repository;

import com.proyecto.PlayApp.entity.Pedido;
import com.proyecto.PlayApp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Encuentra el último pedido de un usuario ordenado por fecha de creación
    Pedido findTopByUsuarioOrderByFechaCreacionDesc(Usuario usuario);
}

