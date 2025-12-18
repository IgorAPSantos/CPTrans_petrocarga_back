package com.cptrans.petrocarga.infrastructure.push;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.application.service.PushNotificationService;
import com.cptrans.petrocarga.models.Notificacao;
import com.cptrans.petrocarga.models.PushToken;
import com.cptrans.petrocarga.repositories.PushTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FirebasePushService implements PushNotificationService {

    @Autowired
    private PushTokenRepository pushTokenRepository;

    @Override
    public void enviarNotificacao(Notificacao notificacao) {
        List<PushToken> tokens = pushTokenRepository.findByUsuarioId(notificacao.getUsuarioId());   

        if (!tokens.isEmpty()) {
            for (PushToken token : tokens) {
                Message message = Message.builder()
                    .setToken(token.getToken())
                    .setNotification(
                        Notification.builder()
                        .setTitle(notificacao.getTitulo())
                        .setBody(notificacao.getMensagem())
                        .build())
                    .putAllData(
                        Map.of(
                            "notificacaoId", notificacao.getId().toString(),
                            "tipo", notificacao.getTipo().name()
                        ))
                    .build();

                FirebaseMessaging.getInstance().sendAsync(message);
            }
        }
    }
}
