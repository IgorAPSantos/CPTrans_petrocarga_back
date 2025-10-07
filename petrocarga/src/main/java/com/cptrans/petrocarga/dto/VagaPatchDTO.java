package com.cptrans.petrocarga.dto;

import com.cptrans.petrocarga.enums.AreaVagaEnum;
import com.cptrans.petrocarga.enums.StatusVagaEnum;
import java.util.UUID;

public class VagaPatchDTO {

    private UUID enderecoId;

    private AreaVagaEnum area;
    private String numeroEndereco;
    private String referenciaEndereco;
    private String tipoVaga;
    private String referenciaGeoInicio;
    private String referenciaGeoFim;
    private Integer maxEixos;
    private Integer comprimento;
    private StatusVagaEnum status;


    public UUID getEnderecoId() {
        return enderecoId;
    }

    public void setEnderecoId(UUID enderecoId) {
        this.enderecoId = enderecoId;
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

    public String getTipoVaga() {
        return tipoVaga;
    }

    public void setTipoVaga(String tipoVaga) {
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

    public Integer getMaxEixos() {
        return maxEixos;
    }

    public void setMaxEixos(Integer maxEixos) {
        this.maxEixos = maxEixos;
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
}