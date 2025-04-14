package com.proyecto.PlayApp.repository;

import com.proyecto.PlayApp.entity.Producto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
    List<Producto> findAll(Specification<Producto> specification);
    List<Producto> findByRestaurante_Id(Long id);
}
