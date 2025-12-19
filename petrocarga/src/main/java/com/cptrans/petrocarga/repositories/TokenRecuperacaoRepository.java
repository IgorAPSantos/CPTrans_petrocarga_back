package com.cptrans.petrocarga.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cptrans.petrocarga.models.TokenRecuperacao;
import com.cptrans.petrocarga.models.Usuario;

public interface TokenRecuperacaoRepository extends JpaRepository<TokenRecuperacao, UUID> {
    Optional<TokenRecuperacao> findByCodigoAndUsuarioAndUtilizadoFalse(String codigo, Usuario usuario);
}
