package com.cptrans.petrocarga.dto;

import com.cptrans.petrocarga.models.EnderecoVaga;
import java.util.UUID;

public class EnderecoVagaResponseDTO {
    private UUID id;
    private String codigoPmp;
    private String logradouro;  
    private String bairro;

    public EnderecoVagaResponseDTO() {
    }

    public EnderecoVagaResponseDTO(EnderecoVaga enderecoVaga) {
        this.id = enderecoVaga.getId();
        this.codigoPmp = enderecoVaga.getCodigoPMP();
        this.logradouro = enderecoVaga.getLogradouro();
        this.bairro = enderecoVaga.getBairro();
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getCodigoPmp() {
        return codigoPmp;
    }
    public void setCodigoPmp(String codigoPmp) {
        this.codigoPmp = codigoPmp;
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
