package com.cptrans.petrocarga.dto;

import java.util.Set;
import java.util.UUID;

import com.cptrans.petrocarga.enums.AreaVagaEnum;
import com.cptrans.petrocarga.enums.StatusVagaEnum;
import com.cptrans.petrocarga.enums.TipoVagaEnum;

public class VagaResponseDTO {

    private UUID id;
    private EnderecoVagaResponseDTO endereco;
    private AreaVagaEnum area;
    private String numeroEndereco;
    private String referenciaEndereco;
    private TipoVagaEnum tipoVaga;
    private String referenciaGeoInicio;
    private String referenciaGeoFim;
    private Integer comprimento;
    private StatusVagaEnum status;
    private Set<OperacaoVagaResponseDTO> operacoesVaga; // Substituído

    // Getters e Setters para todos os campos (incluindo os novos)

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public EnderecoVagaResponseDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoVagaResponseDTO endereco) {
        this.endereco = endereco;
    }

    public AreaVagaEnum getArea() {
        return area;
    }

    public void setArea(AreaVagaEnum area) {
        this.area = area;
    }

    public String getNumeroEndereco() {
        return numeroEndereco;
    }

    public void setNumeroEndereco(String numeroEndereco) {
        this.numeroEndereco = numeroEndereco;
    }

    public String getReferenciaEndereco() {
        return referenciaEndereco;
    }

    public void setReferenciaEndereco(String referenciaEndereco) {
        this.referenciaEndereco = referenciaEndereco;
    }

    public TipoVagaEnum getTipoVaga() {
        return tipoVaga;
    }

    public void setTipoVaga(TipoVagaEnum tipoVaga) {
        this.tipoVaga = tipoVaga;
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

    public Integer getComprimento() {
        return comprimento;
    }

    public void setComprimento(Integer comprimento) {
        this.comprimento = comprimento;
    }

    public StatusVagaEnum getStatus() {
        return status;
    }

    public void setStatus(StatusVagaEnum status) {
        this.status = status;
    }

    public Set<OperacaoVagaResponseDTO> getOperacoesVaga() {
        return operacoesVaga;
    }

    public void setOperacoesVaga(Set<OperacaoVagaResponseDTO> operacoesVaga) {
        this.operacoesVaga = operacoesVaga;
    }
}