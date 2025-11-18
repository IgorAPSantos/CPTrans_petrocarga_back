package com.cptrans.petrocarga.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.enums.TipoVeiculoEnum;
import com.cptrans.petrocarga.models.ReservaRapida;
import com.cptrans.petrocarga.models.Vaga;

import jakarta.validation.constraints.NotNull;

public class ReservaRapidaRequestDTO {

    @NotNull
    private UUID vagaId;

    private TipoVeiculoEnum tipoVeiculo;

    private String placa;

    @NotNull
    private OffsetDateTime inicio;

    @NotNull
    private OffsetDateTime fim;

    public ReservaRapidaRequestDTO() {
        
    }

    public ReservaRapida toEntity(Vaga vaga) {
        ReservaRapida reservaRapida = new ReservaRapida();
        reservaRapida.setVaga(vaga);
        reservaRapida.setTipoVeiculo(this.tipoVeiculo);
        reservaRapida.setPlaca(this.placa);
        reservaRapida.setInicio(this.inicio);
        reservaRapida.setFim(this.fim);
        reservaRapida.setStatus(StatusReservaEnum.ATIVA);
        return reservaRapida;
    }

    // Getters and Setters
    public UUID getVagaId() {
        return vagaId;
    }

    public void setVagaId(UUID vagaId) {
        this.vagaId = vagaId;
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
}
