package com.cptrans.petrocarga.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cptrans.petrocarga.models.Motorista;
import com.cptrans.petrocarga.models.Usuario;

@Repository
public interface MotoristaRepository extends JpaRepository<Motorista, UUID> {
    public Optional<Motorista> findByUsuario(Usuario usuario);
    public Optional<Motorista> findByNumeroCnh(String numeroCnh);
    public Boolean existsByNumeroCnh(String numeroCnh);
}
