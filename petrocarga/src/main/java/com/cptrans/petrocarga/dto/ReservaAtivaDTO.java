package com.cptrans.petrocarga.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.cptrans.petrocarga.models.Vaga;


public class ReservaAtivaDTO {
    private UUID vagaId;
    private EnderecoVagaResponseDTO enderecoVaga;
    private OffsetDateTime inicio;
    private OffsetDateTime fim;
    private Integer tamanhoVeiculo;
    private String placaVeiculo;

    public ReservaAtivaDTO() {
    }
    public ReservaAtivaDTO(Vaga vaga,OffsetDateTime inicio, OffsetDateTime fim, Integer tamanhoVeiculo, String placaVeiculo) {
        this.vagaId = vaga.getId();
        this.enderecoVaga = vaga.getEndereco().toResponseDTO();
        this.inicio = inicio;
        this.fim = fim;
        this.tamanhoVeiculo = tamanhoVeiculo;
        this.placaVeiculo = placaVeiculo;
    }
    public UUID getVaga() {
        return vagaId;
    }
    public void setVaga(Vaga vaga) {
        this.vagaId = vaga.getId();
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
}
