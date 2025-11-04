package com.cptrans.petrocarga.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cptrans.petrocarga.enums.StatusVagaEnum;
import com.cptrans.petrocarga.models.Vaga;


@Repository
public interface  VagaRepository extends JpaRepository<Vaga, UUID> {
    List<Vaga> findByStatus(StatusVagaEnum status);
    Page<Vaga> findByStatus(StatusVagaEnum status, Pageable pageable);
    Page<Vaga> findByEnderecoLogradouroContainingIgnoreCase(String logradouro, Pageable pageable);
    Page<Vaga> findByStatusAndEnderecoLogradouroContainingIgnoreCase(StatusVagaEnum status, String logradouro, Pageable pageable);
}
