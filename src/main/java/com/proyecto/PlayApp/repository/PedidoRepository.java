package com.proyecto.PlayApp.repository;

import com.proyecto.PlayApp.entity.Pedido;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PedidoRepository extends MongoRepository<Pedido, String> {
}
