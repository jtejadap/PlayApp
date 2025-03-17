package com.proyecto.PlayApp.repository;

import com.proyecto.PlayApp.entity.Bebida;
import com.proyecto.PlayApp.entity.TipoBebida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BebidaRepository extends JpaRepository<Bebida, Long> {
    List<Bebida> findByTipo(TipoBebida tipo);
}