package com.cptrans.petrocarga.repositories;

import java.util.Optional;

import com.cptrans.petrocarga.models.Agente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import com.cptrans.petrocarga.models.Usuario;

public interface AgenteRepository extends JpaRepository<Agente, UUID> {
    public Optional<Agente> findByUsuario(Usuario usuario);
    public Optional<Agente> findByMatricula(String matricula);
    public Boolean existsByMatricula(String matricula);
}
