package com.cptrans.petrocarga.application.service;

import com.cptrans.petrocarga.models.Notificacao;

public interface PushNotificationService {
    void enviarNotificacao(Notificacao notificacao);
}
