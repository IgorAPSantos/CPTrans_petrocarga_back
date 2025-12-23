package com.cptrans.petrocarga.application.service;

import java.util.UUID;

import com.cptrans.petrocarga.models.Notificacao;

public interface RealTimeNotificationService {
    void enviarNotificacao(Notificacao notificacao);
    boolean isAtivo(UUID usuarioId);
}
