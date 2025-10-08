package com.cptrans.petrocarga.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cptrans.petrocarga.models.OperacaoVaga;

@Repository
public interface OperacaoVagaRepository extends JpaRepository<OperacaoVaga, UUID>{
    
}
