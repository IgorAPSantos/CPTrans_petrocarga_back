package com.cptrans.petrocarga.domain.event;

import java.time.Instant;

import com.cptrans.petrocarga.models.Notificacao;

public record NotificacaoCriadaEvent( Notificacao notificacao, Instant occurredOn) implements DomainEvent {
    
    public NotificacaoCriadaEvent(Notificacao notificacao) {
        this(notificacao, Instant.now());
    }

}
