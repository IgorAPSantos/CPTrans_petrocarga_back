package com.cptrans.petrocarga.application.port.out;

import com.cptrans.petrocarga.domain.event.DomainEvent;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
