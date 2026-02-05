package com.cptrans.petrocarga.dto.dashboard;

import java.time.OffsetDateTime;
import java.util.UUID;

public class VehicleRouteStopDTO {
    private String source; // RESERVA | RESERVA_RAPIDA
    private OffsetDateTime inicio;
    private OffsetDateTime fim;

    private String cidadeOrigem;
    private String entradaCidade;

    private UUID vagaId;
    private String vagaLabel;

    public VehicleRouteStopDTO() {
    }

    public VehicleRouteStopDTO(String source, OffsetDateTime inicio, OffsetDateTime fim, String cidadeOrigem,
            String entradaCidade, UUID vagaId, String vagaLabel) {
        this.source = source;
        this.inicio = inicio;
        this.fim = fim;
        this.cidadeOrigem = cidadeOrigem;
        this.entradaCidade = entradaCidade;
        this.vagaId = vagaId;
        this.vagaLabel = vagaLabel;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public OffsetDateTime getInicio() {
        return inicio;
    }

    public void setInicio(OffsetDateTime inicio) {
        this.inicio = inicio;
    }

    public OffsetDateTime getFim() {
        return fim;
    }

    public void setFim(OffsetDateTime fim) {
        this.fim = fim;
    }

    public String getCidadeOrigem() {
        return cidadeOrigem;
    }

    public void setCidadeOrigem(String cidadeOrigem) {
        this.cidadeOrigem = cidadeOrigem;
    }

    public String getEntradaCidade() {
        return entradaCidade;
    }

    public void setEntradaCidade(String entradaCidade) {
        this.entradaCidade = entradaCidade;
    }

    public UUID getVagaId() {
        return vagaId;
    }

    public void setVagaId(UUID vagaId) {
        this.vagaId = vagaId;
    }

    public String getVagaLabel() {
        return vagaLabel;
    }

    public void setVagaLabel(String vagaLabel) {
        this.vagaLabel = vagaLabel;
    }
}
