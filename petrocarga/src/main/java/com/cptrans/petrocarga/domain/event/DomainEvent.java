package com.cptrans.petrocarga.domain.event;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredOn();
}
