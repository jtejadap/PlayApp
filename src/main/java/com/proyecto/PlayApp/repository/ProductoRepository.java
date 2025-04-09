package com.proyecto.PlayApp.repository;

import com.proyecto.PlayApp.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByRestaurante_Id(Long id);
}
