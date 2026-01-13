package com.cptrans.petrocarga.repositories;

import java.util.Optional;

import com.cptrans.petrocarga.models.Empresa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {
    public Optional<Empresa> findByUsuarioId(UUID id);
}
