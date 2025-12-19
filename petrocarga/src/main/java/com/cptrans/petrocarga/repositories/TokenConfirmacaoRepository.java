package com.cptrans.petrocarga.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cptrans.petrocarga.models.TokenConfirmacao;

public interface TokenConfirmacaoRepository extends JpaRepository<TokenConfirmacao, UUID> {
    Optional<TokenConfirmacao> findByToken(String token);
    void deleteByToken(String token);
}
