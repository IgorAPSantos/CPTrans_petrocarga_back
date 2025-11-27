package com.cptrans.petrocarga.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.cptrans.petrocarga.enums.StatusReservaEnum;
import com.cptrans.petrocarga.models.Vaga;


public class ReservaDTO {
    private UUID id;
    private UUID vagaId;
    private String numeroEndereco;
    private String referenciaEndereco;
    private EnderecoVagaResponseDTO enderecoVaga;
    private OffsetDateTime inicio;
    private OffsetDateTime fim;
    private Integer tamanhoVeiculo;
    private String placaVeiculo;
    private StatusReservaEnum status;

    public ReservaDTO() {
    }
    public ReservaDTO(UUID reservaId, Vaga vaga, OffsetDateTime inicio, OffsetDateTime fim, Integer tamanhoVeiculo, String placaVeiculo, StatusReservaEnum status) {
        this.id = reservaId;
        this.vagaId = vaga.getId();
        this.numeroEndereco = vaga.getNumeroEndereco();
        this.referenciaEndereco = vaga.getReferenciaEndereco();
        this.enderecoVaga = vaga.getEndereco().toResponseDTO();
        this.inicio = inicio;
        this.fim = fim;
        this.tamanhoVeiculo = tamanhoVeiculo;
        this.placaVeiculo = placaVeiculo;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID reservaId) {
        this.id = reservaId;
    }
    public UUID getVaga() {
        return vagaId;
    }
    public void setVaga(Vaga vaga) {
        this.vagaId = vaga.getId();
    }
    public String getNumeroEndereco() {
        return numeroEndereco;
    }
    public void setNumeroEndereco(Vaga vaga) {
        this.numeroEndereco = vaga.getNumeroEndereco();
    }
    public String getReferenciaEndereco() {
        return referenciaEndereco;
    }
    public void setReferenciaEndereco(Vaga vaga) {
        this.referenciaEndereco = vaga.getReferenciaEndereco();
    }
    public EnderecoVagaResponseDTO getEnderecoVaga() {
        return enderecoVaga;
    }
    public void setEnderecoVaga(Vaga vaga) {
        this.enderecoVaga = vaga.getEndereco().toResponseDTO();
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
    public Integer getTamanhoVeiculo() {
        return tamanhoVeiculo;
    }
    public void setTamanhoVeiculo(Integer tamanhoVeiculo) {
        this.tamanhoVeiculo = tamanhoVeiculo;
    }
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }
    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }
    public StatusReservaEnum getStatus() {
        return status;
    }
    public void setStatus(StatusReservaEnum status) {
        this.status = status;
    }
}
