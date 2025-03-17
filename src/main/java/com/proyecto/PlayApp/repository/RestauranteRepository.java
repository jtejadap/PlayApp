package com.proyecto.PlayApp.repository;

import com.proyecto.PlayApp.entity.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
    Optional<Restaurante> findByCorreoRestaurante(String correoRestaurante); // Para login

}
