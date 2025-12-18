package com.cptrans.petrocarga.domain.event;

import java.time.Instant;
import java.util.UUID;

import com.cptrans.petrocarga.models.Notificacao;

public record ReservaCriadaEvent (UUID reservaId, UUID motoristaId, Notificacao notificacao, Instant occurredOn) implements DomainEvent {
    
    public ReservaCriadaEvent(UUID reservaId, UUID motoristaId, Notificacao notificacao) {
        this(reservaId, motoristaId, notificacao, Instant.now());
    }
}
