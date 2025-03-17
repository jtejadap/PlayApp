package com.proyecto.PlayApp.repository;

import com.proyecto.PlayApp.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
}