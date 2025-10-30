package com.cptrans.petrocarga.repositories;

import java.util.Optional;

import com.cptrans.petrocarga.models.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    public Optional<Usuario> findByEmail(String email);
    public Optional<Usuario> findByCpf(String cpf);
    public Boolean existsByEmail(String email);
    public Boolean existsByCpf(String cpf);
}
