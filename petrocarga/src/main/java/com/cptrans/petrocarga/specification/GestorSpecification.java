package com.cptrans.petrocarga.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.cptrans.petrocarga.dto.GestorFiltrosDTO;
import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.models.Usuario;

import jakarta.persistence.criteria.Predicate;

public class GestorSpecification {
     public static Specification<Usuario> filtrar(
        GestorFiltrosDTO filtros
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

             predicates.add(
                    cb.equal(root.get("permissao"), PermissaoEnum.GESTOR)
                );

            if (filtros.nome() != null) {
                predicates.add(
                    cb.like(cb.lower(root.get("nome")), "%" + filtros.nome().trim().toLowerCase() + "%")
                );
            }

            if (filtros.cpf() != null) {
                predicates.add(
                    cb.equal(root.get("cpf"), filtros.cpf())
                );
            }

            if (filtros.email() != null) {
                predicates.add(
                    cb.equal(root.get("email"), filtros.email())
                );
            }

            if (filtros.ativo() != null) {
                predicates.add(
                    cb.equal(root.get("ativo"), filtros.ativo())
                );
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
