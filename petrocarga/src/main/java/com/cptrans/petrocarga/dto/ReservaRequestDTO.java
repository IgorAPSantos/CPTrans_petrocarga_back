package com.cptrans.petrocarga.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.models.Motorista;
import com.cptrans.petrocarga.models.Reserva;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.models.Veiculo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReservaRequestDTO {
    @NotNull
    private UUID vagaId;
    @NotNull
    private UUID motoristaId;
    @NotNull
    private UUID veiculoId;
    @NotBlank
    private String cidadeOrigem;
    @NotNull
    private OffsetDateTime inicio;
    @NotNull
    private OffsetDateTime fim;
    @NotNull
    private StatusReservaEnum status;

    public Reserva toEntity(Vaga vaga, Motorista motorista, Veiculo veiculo, Usuario criadoPor) {
        Reserva reserva = new Reserva();
        reserva.setVaga(vaga);
        reserva.setMotorista(motorista);
        reserva.setVeiculo(veiculo);
        reserva.setCriadoPor(criadoPor);
        reserva.setCidadeOrigem(this.cidadeOrigem);
        reserva.setInicio(this.inicio);
        reserva.setFim(this.fim);
        reserva.setStatus(this.status);
        return reserva;
    }

    public Reserva toEntity() {
        Reserva reserva = new Reserva();
        reserva.setCidadeOrigem(this.cidadeOrigem);
        reserva.setInicio(this.inicio);
        reserva.setFim(this.fim);
        reserva.setStatus(this.status);
        return reserva;
    }

    // Getters and Setters
    public UUID getVagaId() {
        return vagaId;
    }
    public UUID getMotoristaId() {
        return motoristaId;
    }
    public UUID getVeiculoId() {
        return veiculoId;
    }
    public String getCidadeOrigem() {
        return cidadeOrigem;
    }
    public OffsetDateTime getInicio() {
        return inicio;
    }
    public OffsetDateTime getFim() {
        return fim;
    }
    public StatusReservaEnum getStatus() {
        return status;
    }
}
