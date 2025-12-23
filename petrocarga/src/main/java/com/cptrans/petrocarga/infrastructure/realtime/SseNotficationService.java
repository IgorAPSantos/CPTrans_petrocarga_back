package com.cptrans.petrocarga.infrastructure.realtime;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.cptrans.petrocarga.application.service.RealTimeNotificationService;
import com.cptrans.petrocarga.models.Notificacao;

@Service
public class SseNotficationService implements RealTimeNotificationService {

    private final Map<UUID, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(UUID usuarioId) {
        SseEmitter emitter = new SseEmitter(0L);

        emitters.computeIfAbsent(usuarioId, id -> ConcurrentHashMap.newKeySet()).add(emitter);

        Runnable cleanup = () -> {
            Set<SseEmitter> set = emitters.get(usuarioId);
            if (set != null) {
                set.remove(emitter);
                if (set.isEmpty()) emitters.remove(usuarioId);
            }
        };

        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

         try {
            emitter.send(
                SseEmitter.event()
                .name("INIT")
                .data("connected"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        startHeartbeat(emitter, usuarioId);
       
        return emitter;
    }

    private void startHeartbeat(SseEmitter emitter, UUID usuarioId) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().comment("keep-alive"));
            } catch (Exception e) {
                scheduler.shutdown();
                Set<SseEmitter> set = emitters.get(usuarioId);
                if (set != null) set.remove(emitter);
            }
        }, 15, 15, java.util.concurrent.TimeUnit.SECONDS);
    }


    @Override
    public void enviarNotificacao(Notificacao notificacao) {
        Set<SseEmitter> userEmitters = emitters.get(notificacao.getUsuarioId());
        
        if (userEmitters == null || userEmitters.isEmpty()) return;

        for(SseEmitter emitter : Set.copyOf(userEmitters)) {
            try {
                emitter.send(
                    SseEmitter.event()
                        .id(notificacao.getId().toString())
                        .name("notificacao")
                        .data(notificacao)
                        .reconnectTime(3000)
                    );
            } catch (Exception e) {
                userEmitters.remove(emitter);
            }
        }
        if (userEmitters.isEmpty()) emitters.remove(notificacao.getUsuarioId());
    }

    @Override
    public boolean isAtivo(UUID usuarioId) {
        return emitters.containsKey(usuarioId) && !emitters.get(usuarioId).isEmpty();
    }
    
}
