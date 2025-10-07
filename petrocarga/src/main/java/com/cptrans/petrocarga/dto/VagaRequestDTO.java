package com.cptrans.petrocarga.dto;

import java.util.Set;
import com.cptrans.petrocarga.enums.AreaVagaEnum;
import com.cptrans.petrocarga.enums.StatusVagaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class VagaRequestDTO {

    @Valid
    @NotNull(message = "O endereço é obrigatório.")
    private EnderecoVagaRequestDTO endereco;
    
    @Schema(description = "Área da vaga (Ex: AMARELA, VERMELHA)")
    private AreaVagaEnum area;
    
    @Schema(description = "Número da vaga no endereço", example = "Vaga 03")
    private String numeroEndereco;

    @Schema(description = "Ponto de referência para a vaga", example = "Em frente ao portão principal")
    private String referenciaEndereco;

    @Schema(description = "Tipo de vaga (Ex: CARGA, DESCARGA)")
    private String tipoVaga;

    @Schema(description = "Coordenada geográfica inicial da vaga", example = "-22.509135, -43.171351")
    private String referenciaGeoInicio;
    
    @Schema(description = "Coordenada geográfica final da vaga (se aplicável)", example = "-22.509140, -43.171355")
    private String referenciaGeoFim;

    @Valid
    @NotNull(message = "O número máximo de eixos é obrigatório.")
    @Schema(description = "Número máximo de eixos permitidos para a vaga", example = "2")
    private Integer maxEixos;
    
    @Valid
    @NotNull(message = "O comprimento é obrigatório.")
    @Schema(description = "Comprimento máximo em metros permitido para a vaga", example = "12")
    private Integer comprimento;
    
    @Schema(description = "Status inicial da vaga (Ex: DISPONIVEL, OCUPADA)")
    private StatusVagaEnum status;

    @Valid
    @Schema(description = "Lista com os dias e horários de funcionamento da vaga")
    private Set<OperacaoVagaRequestDTO> operacoesVaga;


    // Getters e Setters
    
    public EnderecoVagaRequestDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoVagaRequestDTO endereco) {
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

    public Set<OperacaoVagaRequestDTO> getOperacoesVaga() {
        return operacoesVaga;
    }

    public void setOperacoesVaga(Set<OperacaoVagaRequestDTO> operacoesVaga) {
        this.operacoesVaga = operacoesVaga;
    }
}