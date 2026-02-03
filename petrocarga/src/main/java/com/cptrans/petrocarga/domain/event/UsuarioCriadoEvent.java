package com.cptrans.petrocarga.domain.event;

import java.time.Instant;

public record UsuarioCriadoEvent (String email, String codigo, Instant occurredOn)  implements DomainEvent{
    public UsuarioCriadoEvent(String email, String codigo) {
        this(email, codigo, Instant.now());
    }
}
