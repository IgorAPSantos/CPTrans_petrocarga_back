package com.cptrans.petrocarga.dto;

import java.util.UUID;

public class EnderecoVagaResponseDTO {
    private UUID id;
    private String codidoPmp;
    private String logradouro;  
    private String bairro;

    public EnderecoVagaResponseDTO() {
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getCodidoPmp() {
        return codidoPmp;
    }
    public void setCodidoPmp(String codidoPmp) {
        this.codidoPmp = codidoPmp;
    }
    public String getLogradouro() {
        return logradouro;
    }
    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }
    public String getBairro() {
        return bairro;
    }
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }
}
