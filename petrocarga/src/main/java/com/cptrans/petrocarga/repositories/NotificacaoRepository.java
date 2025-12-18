package com.cptrans.petrocarga.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cptrans.petrocarga.models.Notificacao;

public interface NotificacaoRepository extends JpaRepository<Notificacao, UUID > {
    public List<Notificacao> findByUsuarioId(UUID usuarioId);
    public Optional<Notificacao> findByIdAndUsuarioId(UUID notificacaoId, UUID usuarioId);
    public List<Notificacao> findByUsuarioIdAndLida(UUID usuarioId, boolean lida);
}
