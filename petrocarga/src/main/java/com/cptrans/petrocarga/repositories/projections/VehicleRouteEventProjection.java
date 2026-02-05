package com.cptrans.petrocarga.repositories.projections;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface VehicleRouteEventProjection {
    String getPlaca();

    String getSource();

    OffsetDateTime getInicio();

    OffsetDateTime getFim();

    String getCidadeOrigem();

    String getEntradaCidade();

    UUID getVagaId();

    String getVagaLabel();
}
