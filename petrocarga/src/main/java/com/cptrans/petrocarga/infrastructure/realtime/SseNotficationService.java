package com.cptrans.petrocarga.infrastructure.realtime;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.cptrans.petrocarga.application.service.RealTimeNotificationService;
import com.cptrans.petrocarga.models.Notificacao;

@Service
public class SseNotficationService implements RealTimeNotificationService {

    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(UUID usuarioId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(usuarioId, emitter);

        emitter.onCompletion(() -> emitters.remove(usuarioId));
        emitter.onTimeout(() -> emitters.remove(usuarioId));
        emitter.onError((e) -> emitters.remove(usuarioId));

        return emitter;
    }


    @Override
    public void enviarNotificacao(Notificacao notificacao) {
        SseEmitter emitter = emitters.get(notificacao.getUsuarioId());
        
        if (emitter != null) {
            try {
                emitter.send(notificacao);
            } catch (Exception e) {
                emitters.remove(notificacao.getUsuarioId());
            }
        }
    }
    
}
