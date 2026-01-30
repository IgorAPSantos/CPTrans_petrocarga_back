package com.cptrans.petrocarga.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.models.Motorista;
import com.cptrans.petrocarga.models.Reserva;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.models.Veiculo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReservaRequestDTO {
    @NotNull(message = "O campo 'vagaId' é obrigatório.")
    private UUID vagaId;
    @NotNull(message = "O campo 'motoristaId' é obrigatório.")
    private UUID motoristaId;
    @NotNull(message = "O campo 'veiculoId' é obrigatório.")
    private UUID veiculoId;
    @NotNull(message = "O campo 'cidadeOrigem' é obrigatório.")
    @NotBlank(message = "O campo 'cidadeOrigem' não pode estar em branco.")
    private String cidadeOrigem;
    @NotBlank(message = "O campo 'entradaCidade' não pode estar em branco.")
    private String entradaCidade;
    @NotNull(message = "O campo 'inicio' é obrigatório.")
    private OffsetDateTime inicio;
    @NotNull(message = "O campo 'fim' é obrigatório.")
    private OffsetDateTime fim;


    public Reserva toEntity(Vaga vaga, Motorista motorista, Veiculo veiculo) {
        Reserva reserva = new Reserva();
        reserva.setVaga(vaga);
        reserva.setMotorista(motorista);
        reserva.setVeiculo(veiculo);
        reserva.setCidadeOrigem(this.cidadeOrigem);
        reserva.setEntradaCidade(this.entradaCidade);
        reserva.setInicio(this.inicio);
        reserva.setFim(this.fim);
        return reserva;
    }

    public Reserva toEntity() {
        Reserva reserva = new Reserva();
        reserva.setCidadeOrigem(this.cidadeOrigem);
        reserva.setEntradaCidade(this.entradaCidade);
        reserva.setInicio(this.inicio);
        reserva.setFim(this.fim);
        reserva.setStatus(StatusReservaEnum.ATIVA);
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
    public String getEntradaCidade() {
        return entradaCidade;
    }
    public OffsetDateTime getInicio() {
        return inicio;
    }
    public OffsetDateTime getFim() {
        return fim;
    }
}
