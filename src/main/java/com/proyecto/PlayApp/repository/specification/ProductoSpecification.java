package com.proyecto.PlayApp.repository.specification;

import com.proyecto.PlayApp.dto.FiltrosDTO;
import com.proyecto.PlayApp.entity.Producto;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;


public class ProductoSpecification {
    public static Specification<Producto> getSpecification(FiltrosDTO filtro) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filtro.getNombre() != null) {
                predicates.add(criteriaBuilder.like(root.get("nombre"),"%"+filtro.getNombre()+"%"));
            }

            if (filtro.getPrecio() != null) {
                predicates.add(criteriaBuilder.equal(root.get("precio"),filtro.getPrecio()));
            }

            if (filtro.getStock() != null) {
                predicates.add(criteriaBuilder.equal(root.get("stock"),filtro.getStock()));
            }

            if (filtro.getCategoria() != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoria"),filtro.getCategoria()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

}
