package com.proyecto.PlayApp.repository;

import com.proyecto.PlayApp.entity.Producto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends MongoRepository<Producto, String>{
}
