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
    
    // Shared scheduler for all heartbeats - single thread is enough
    private final ScheduledExecutorService heartbeatScheduler = 
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "SSE-Heartbeat");
                t.setDaemon(true);
                return t;
            });

    public SseEmitter connect(UUID usuarioId) {
        // 30 minute timeout instead of infinite (0L)
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        emitters.computeIfAbsent(usuarioId, id -> ConcurrentHashMap.newKeySet()).add(emitter);

        Runnable cleanup = () -> cleanupEmitter(emitter, usuarioId);

        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> {
            // Log only if it's not a client disconnect
            if (!isClientDisconnect(e)) {
                log.warn("SSE error for user {}: {}", usuarioId, e.getMessage());
            }
            cleanup.run();
        });

        try {
            emitter.send(
                SseEmitter.event()
                    .name("INIT")
                    .data("connected"));
        } catch (IOException e) {
            log.debug("Failed to send INIT to user {}: {}", usuarioId, e.getMessage());
            cleanupEmitter(emitter, usuarioId);
            emitter.completeWithError(e);
        }

        startHeartbeat(emitter, usuarioId);
       
        return emitter;
    }

    private void cleanupEmitter(SseEmitter emitter, UUID usuarioId) {
        // Cancel heartbeat task first
        ScheduledFuture<?> task = heartbeatTasks.remove(emitter);
        if (task != null) {
            task.cancel(false);
        }
        
        // Remove from emitters map
        Set<SseEmitter> set = emitters.get(usuarioId);
        if (set != null) {
            set.remove(emitter);
            if (set.isEmpty()) {
                emitters.remove(usuarioId);
            }
        }
    }

    private void startHeartbeat(SseEmitter emitter, UUID usuarioId) {
        ScheduledFuture<?> task = heartbeatScheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().comment("keep-alive"));
            } catch (Exception e) {
                // Client disconnected - cleanup silently
                cleanupEmitter(emitter, usuarioId);
                try {
                    emitter.complete();
                } catch (Exception ignored) {
                    // Already completed or errored
                }
            }
        }, 15, 15, TimeUnit.SECONDS);
        
        heartbeatTasks.put(emitter, task);
    }
    
    private boolean isClientDisconnect(Throwable e) {
        String message = e.getMessage();
        if (message == null) return false;
        return message.contains("Broken pipe") 
            || message.contains("Connection reset")
            || message.contains("ClientAbortException");
    }
    
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down SSE heartbeat scheduler...");
        heartbeatScheduler.shutdown();
        heartbeatTasks.clear();
        emitters.clear();
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
