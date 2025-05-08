package com.proyecto.PlayApp.repository.specification;

import com.proyecto.PlayApp.dto.FiltrosDTO;
import com.proyecto.PlayApp.entity.Pedido;
import com.proyecto.PlayApp.entity.Usuario;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PedidoSpecification {
    public static Specification<Pedido> getSpecification(FiltrosDTO filtro) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            Join<Pedido, Usuario> relatedEntityJoin = root.join("usuario", JoinType.INNER);

            if (filtro.getCategoria() != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), filtro.getCategoria()));
            }

            if (filtro.getId() != null) {
                predicates.add(criteriaBuilder.equal(relatedEntityJoin.get("id"), filtro.getId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
