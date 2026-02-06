package com.cptrans.petrocarga.infrastructure.realtime;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.cptrans.petrocarga.application.service.RealTimeNotificationService;
import com.cptrans.petrocarga.models.Notificacao;

import jakarta.annotation.PreDestroy;

@Service
public class SseNotficationService implements RealTimeNotificationService {

    private static final Logger log = LoggerFactory.getLogger(SseNotficationService.class);

    private final Map<UUID, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final Map<SseEmitter, ScheduledFuture<?>> heartbeatTasks = new ConcurrentHashMap<>();

    private final ScheduledExecutorService heartbeatScheduler =
        Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "SSE-Heartbeat");
            t.setDaemon(true);
            return t;
        });

    public SseEmitter connect(UUID usuarioId) {
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(30));

        emitters
            .computeIfAbsent(usuarioId, id -> ConcurrentHashMap.newKeySet())
            .add(emitter);

        Runnable cleanup = () -> cleanupEmitter(usuarioId, emitter);

        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> {
            if (!isClientDisconnect(e)) {
                log.warn("SSE error for user {}: {}", usuarioId, e.getMessage());
            }
            cleanup.run();
        });

        try {
            emitter.send(
                SseEmitter.event()
                    .name("INIT")
                    .data("connected")
            );
        } catch (IOException e) {
            log.debug("Failed to send INIT to user {}: {}", usuarioId, e.getMessage());
            cleanup.run();
            emitter.complete();
            return emitter;
        }

        startHeartbeat(usuarioId, emitter);
        return emitter;
    }

    private void startHeartbeat(UUID usuarioId, SseEmitter emitter) {
        ScheduledFuture<?> task = heartbeatScheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(
                    SseEmitter.event()
                        .name("heartbeat")
                        .data("ping")
                );
            } catch (IOException e) {
                cleanupEmitter(usuarioId, emitter);
            }
        }, 15, 15, TimeUnit.SECONDS);

        heartbeatTasks.put(emitter, task);
    }

    private void cleanupEmitter(UUID usuarioId, SseEmitter emitter) {
        ScheduledFuture<?> task = heartbeatTasks.remove(emitter);
        if (task != null) {
            task.cancel(false);
        }

        Set<SseEmitter> set = emitters.get(usuarioId);
        if (set != null) {
            set.remove(emitter);
            if (set.isEmpty()) {
                emitters.remove(usuarioId);
            }
        }

        try {
            emitter.complete();
        } catch (Exception ignored) {}
    }

    private boolean isClientDisconnect(Throwable e) {
        String message = e.getMessage();
        if (message == null) return false;

        return message.contains("Broken pipe")
            || message.contains("Connection reset")
            || message.contains("ClientAbortException");
    }

    @Override
    public void enviarNotificacao(Notificacao notificacao) {
        Set<SseEmitter> userEmitters = emitters.get(notificacao.getUsuarioId());

        if (userEmitters == null || userEmitters.isEmpty()) return;

        for (SseEmitter emitter : Set.copyOf(userEmitters)) {
            try {
                emitter.send(
                    SseEmitter.event()
                        .id(notificacao.getId().toString())
                        .name("notificacao")
                        .data(notificacao)
                        .reconnectTime(3000)
                );
            } catch (IOException e) {
                cleanupEmitter(notificacao.getUsuarioId(), emitter);
            }
        }
    }

    @Override
    public boolean isAtivo(UUID usuarioId) {
        return emitters.containsKey(usuarioId)
            && !emitters.get(usuarioId).isEmpty();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down SSE service...");
        heartbeatScheduler.shutdownNow();
        heartbeatTasks.clear();
        emitters.clear();
    }
}
