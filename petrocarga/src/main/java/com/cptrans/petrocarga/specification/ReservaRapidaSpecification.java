package com.cptrans.petrocarga.specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.models.ReservaRapida;

import jakarta.persistence.criteria.Predicate;


public class ReservaRapidaSpecification {
    public static Specification<ReservaRapida> filtrar(
        UUID usuarioId,
        UUID vagaId,
        String placaVeiculo,
        LocalDate data,
        List<StatusReservaEnum> listaStatus
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                cb.equal(root.get("agente").get("usuario").get("id"), usuarioId)
            );

            if (vagaId != null) {
                predicates.add(
                    cb.equal(root.get("vaga").get("id"), vagaId)
                );
            }

            if (placaVeiculo != null) {
                predicates.add(
                    cb.equal(root.get("placa"), placaVeiculo.toUpperCase())
                );
            }

            if (data != null) {
                predicates.add(
                    cb.equal(
                        cb.function("DATE", LocalDate.class, root.get("inicio")),
                        data
                    )
                );
            }

            if (listaStatus != null && !listaStatus.isEmpty()) {
                predicates.add(
                    root.get("status").in(listaStatus)
                );
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
