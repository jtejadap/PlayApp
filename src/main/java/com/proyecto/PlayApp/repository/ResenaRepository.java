package com.proyecto.PlayApp.repository;

import com.proyecto.PlayApp.entity.Resena;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ResenaRepository extends MongoRepository<Resena, String> {
    List<Resena> findAllByOrderByFechaDesc(); // Para obtener reseñas ordenadas por fecha
}