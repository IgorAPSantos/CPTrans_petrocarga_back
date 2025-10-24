package com.cptrans.petrocarga.dto;

import com.cptrans.petrocarga.enums.TipoVeiculoEnum;
import com.cptrans.petrocarga.models.ReservaRapida;
import java.time.OffsetDateTime;
import java.util.UUID;

public class ReservaRapidaResponseDTO {

    private UUID id;
    private UUID vagaId;
    private UUID agenteId;
    private TipoVeiculoEnum tipoVeiculo;
    private String placa;
    private OffsetDateTime inicio;
    private OffsetDateTime fim;
    private OffsetDateTime criadoEm;

    public ReservaRapidaResponseDTO() {
    }

    public ReservaRapidaResponseDTO(ReservaRapida reservaRapida) {
        this.id = reservaRapida.getId();
        this.vagaId = reservaRapida.getVaga().getId();
        this.agenteId = reservaRapida.getAgente().getId();
        this.tipoVeiculo = reservaRapida.getTipoVeiculo();
        this.placa = reservaRapida.getPlaca();
        this.inicio = reservaRapida.getInicio();
        this.fim = reservaRapida.getFim();
        this.criadoEm = reservaRapida.getCriadoEm();
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

    public UUID getAgenteId() {
        return agenteId;
    }

    public void setAgenteId(UUID agenteId) {
        this.agenteId = agenteId;
    }

    public TipoVeiculoEnum getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(TipoVeiculoEnum tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
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

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
