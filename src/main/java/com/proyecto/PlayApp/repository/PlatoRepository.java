package com.proyecto.PlayApp.repository;

import com.proyecto.PlayApp.entity.Plato;
import com.proyecto.PlayApp.entity.TipoPlato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlatoRepository extends JpaRepository<Plato, Long> {
    List<Plato> findByTipo(TipoPlato tipo);
}
