package com.cptrans.petrocarga.application.service;

import com.cptrans.petrocarga.models.Notificacao;

public interface RealTimeNotificationService {
    void enviarNotificacao(Notificacao notificacao);
}
