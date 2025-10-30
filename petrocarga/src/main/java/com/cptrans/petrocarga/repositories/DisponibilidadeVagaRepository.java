package com.cptrans.petrocarga.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cptrans.petrocarga.models.DisponibilidadeVaga;


public interface DisponibilidadeVagaRepository extends JpaRepository<DisponibilidadeVaga, UUID> {
    public List<DisponibilidadeVaga> findByVagaId(UUID vagaId);
}
