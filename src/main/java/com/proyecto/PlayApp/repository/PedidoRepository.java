package com.proyecto.PlayApp.repository;

import com.proyecto.PlayApp.entity.Pedido;
import com.proyecto.PlayApp.entity.Producto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {
    List<Pedido> findAll(Specification<Pedido> specification);
    List<Pedido> findByUsuario_id(Long id);
}
