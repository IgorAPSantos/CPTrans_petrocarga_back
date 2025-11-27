package com.cptrans.petrocarga.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.models.Reserva;

public class ReservaDetailedResponseDTO {

    private UUID id;
    private UUID vagaId;
    private String logradouro;
    private String bairro;

    private UUID motoristaId;
    private String motoristaNome;

    private UUID veiculoId;
    private String veiculoPlaca;
    private String veiculoModelo; // Adicionado campo modelo

    private UUID criadoPorId;
    private String criadoPorNome;

    private String cidadeOrigem;
    private OffsetDateTime criadoEm;
    private OffsetDateTime inicio;
    private OffsetDateTime fim;
    private StatusReservaEnum status;

    public ReservaDetailedResponseDTO() {}

    public ReservaDetailedResponseDTO(Reserva r) {
        this.id = r.getId();
        if (r.getVaga() != null) {
            if (r.getVaga().getEndereco() != null) {
                this.logradouro = r.getVaga().getEndereco().getLogradouro();
                this.bairro = r.getVaga().getEndereco().getBairro();
            }
        }
        if (r.getMotorista() != null) {
            if (r.getMotorista().getUsuario() != null) {
                this.motoristaNome = r.getMotorista().getUsuario().getNome();
            }
        }
        if (r.getVeiculo() != null) {
            this.veiculoPlaca = r.getVeiculo().getPlaca();
            this.veiculoModelo = r.getVeiculo().getModelo(); // Adicionado campo modelo
        }
        if (r.getCriadoPor() != null) {
            this.criadoPorNome = r.getCriadoPor().getNome();
        }
        this.cidadeOrigem = r.getCidadeOrigem();
        this.criadoEm = r.getCriadoEm();
        this.inicio = r.getInicio();
        this.fim = r.getFim();
        this.status = r.getStatus();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getVagaId() { return vagaId; }
    public void setVagaId(UUID vagaId) { this.vagaId = vagaId; }
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public UUID getMotoristaId() {
        return motoristaId;
    }
    public void setMotoristaId(UUID motoristaId) { this.motoristaId = motoristaId; }
    public String getMotoristaNome() { return motoristaNome; }
    public void setMotoristaNome(String motoristaNome) { this.motoristaNome = motoristaNome; }
    public UUID getVeiculoId() {
        return veiculoId;
    }
    public void setVeiculoId(UUID veiculoId) { this.veiculoId = veiculoId; }
    public String getVeiculoPlaca() { return veiculoPlaca; }
    public void setVeiculoPlaca(String veiculoPlaca) { this.veiculoPlaca = veiculoPlaca; }
    public UUID getCriadoPorId() {
        return criadoPorId;
    }
    public void setCriadoPorId(UUID criadoPorId) { this.criadoPorId = criadoPorId; }
    public String getCriadoPorNome() { return criadoPorNome; }
    public void setCriadoPorNome(String criadoPorNome) { this.criadoPorNome = criadoPorNome; }
    public String getCidadeOrigem() { return cidadeOrigem; }
    public void setCidadeOrigem(String cidadeOrigem) { this.cidadeOrigem = cidadeOrigem; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
    public OffsetDateTime getInicio() { return inicio; }
    public void setInicio(OffsetDateTime inicio) { this.inicio = inicio; }
    public OffsetDateTime getFim() { return fim; }
    public void setFim(OffsetDateTime fim) { this.fim = fim; }
    public StatusReservaEnum getStatus() { return status; }
    public void setStatus(StatusReservaEnum status) { this.status = status; }
    public String getVeiculoModelo() {
        return veiculoModelo;
    }

    public void setVeiculoModelo(String veiculoModelo) {
        this.veiculoModelo = veiculoModelo;
    }
}
