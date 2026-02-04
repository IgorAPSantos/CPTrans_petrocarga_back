package com.cptrans.petrocarga.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.cptrans.petrocarga.dto.MotoristaFiltrosDTO;
import com.cptrans.petrocarga.models.Motorista;

import jakarta.persistence.criteria.Predicate;

public class MotoristaSpecification {
     public static Specification<Motorista> filtrar(
        MotoristaFiltrosDTO filtros
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filtros.nome() != null) {
                predicates.add(
                    cb.like(cb.lower(root.get("usuario").get("nome")), "%" + filtros.nome().trim().toLowerCase() + "%")
                );
            }

            if (filtros.telefone() != null) {
                predicates.add(
                    cb.equal(root.get("usuario").get("telefone"), filtros.telefone())
                );
            }

            if (filtros.cnh() != null) {
                predicates.add(
                    cb.equal(root.get("numero_cnh"), filtros.cnh())
                );
            }

            if (filtros.ativo() != null) {
                predicates.add(
                    cb.equal(root.get("usuario").get("ativo") , filtros.ativo())
                );
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
