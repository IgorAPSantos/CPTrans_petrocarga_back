package com.cptrans.petrocarga.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.models.Reserva;

public class ReservaResponseDTO {

    private UUID id;
    private UUID vagaId;
    private String referenciaGeoInicio;
    private String referenciaGeoFim;
    private UUID motoristaId;
    private UUID veiculoId;
    private UUID criadoPorId;
    private String cidadeOrigem;
    private OffsetDateTime criadoEm;
    private OffsetDateTime inicio;
    private OffsetDateTime fim;
    private StatusReservaEnum status;

    public ReservaResponseDTO() {
    }

    public ReservaResponseDTO(Reserva reserva) {
        this.id = reserva.getId();
        this.vagaId = reserva.getVaga().getId();
        this.referenciaGeoInicio = reserva.getVaga().getReferenciaGeoInicio();
        this.referenciaGeoFim = reserva.getVaga().getReferenciaGeoFim();
        this.motoristaId = reserva.getMotorista().getId();
        this.veiculoId = reserva.getVeiculo().getId();
        this.criadoPorId = reserva.getCriadoPor().getId();
        this.cidadeOrigem = reserva.getCidadeOrigem();
        this.criadoEm = reserva.getCriadoEm();
        this.inicio = reserva.getInicio();
        this.fim = reserva.getFim();
        this.status = reserva.getStatus();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getVagaId() {
        return vagaId;
    }

    public void setVagaId(UUID vagaId) {
        this.vagaId = vagaId;
    }

    public String getReferenciaGeoInicio() {
        return referenciaGeoInicio;
    }

    public void setReferenciaGeoInicio(String referenciaGeoInicio) {
        this.referenciaGeoInicio = referenciaGeoInicio;
    }

    public String getReferenciaGeoFim() {
        return referenciaGeoFim;
    }

    public void setReferenciaGeoFim(String referenciaGeoFim) {
        this.referenciaGeoFim = referenciaGeoFim;
    }

    public UUID getMotoristaId() {
        return motoristaId;
    }

    public void setMotoristaId(UUID motoristaId) {
        this.motoristaId = motoristaId;
    }

    public UUID getVeiculoId() {
        return veiculoId;
    }

    public void setVeiculoId(UUID veiculoId) {
        this.veiculoId = veiculoId;
    }

    public UUID getCriadoPorId() {
        return criadoPorId;
    }

    public void setCriadoPorId(UUID criadoPorId) {
        this.criadoPorId = criadoPorId;
    }

    public String getCidadeOrigem() {
        return cidadeOrigem;
    }

    public void setCidadeOrigem(String cidadeOrigem) {
        this.cidadeOrigem = cidadeOrigem;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
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

    public StatusReservaEnum getStatus() {
        return status;
    }

    public void setStatus(StatusReservaEnum status) {
        this.status = status;
    }
}
