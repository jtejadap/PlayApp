package com.proyecto.PlayApp.repository;

import com.proyecto.PlayApp.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    // Puedes agregar métodos personalizados de consulta si es necesario
}
