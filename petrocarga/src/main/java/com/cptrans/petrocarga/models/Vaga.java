package com.cptrans.petrocarga.models;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.cptrans.petrocarga.dto.VagaResponseDTO;
import com.cptrans.petrocarga.enums.AreaVagaEnum;
import com.cptrans.petrocarga.enums.StatusVagaEnum;
import com.cptrans.petrocarga.enums.TipoVagaEnum;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "vaga")
public class Vaga {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "endereco_id", nullable = false)
    @JsonManagedReference
    private EnderecoVaga endereco;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AreaVagaEnum area;

    @Column(name = "numero_endereco")
    private String numeroEndereco;

    @Column(name = "referencia_endereco")
    private String referenciaEndereco;

    @Column(name = "tipo_vaga", nullable=false)
    @Enumerated(EnumType.STRING)
    private TipoVagaEnum tipoVaga;
    
    @Column(name = "referencia_geo_inicio")
    private String referenciaGeoInicio;

    @Column(name = "referencia_geo_fim")
    private String referenciaGeoFim;
    
    @Schema(description = "Comprimento máximo da vaga em metros", example = "5", minimum = "5", maximum = "30")
    @Column(nullable=false)
    private Integer comprimento;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusVagaEnum status;

    @OneToMany(mappedBy = "vaga", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<OperacaoVaga> operacoesVaga;

    // GETTERS, SETTERS, CONSTRUTOR
    
    public Vaga() {
        this.area = AreaVagaEnum.AMARELA;
        this.status = StatusVagaEnum.DISPONIVEL;
    }

    public UUID getId() {
        return id;
    }

    public EnderecoVaga getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoVaga endereco) {
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

    public Set<OperacaoVaga> getOperacoesVaga() {
        return operacoesVaga;
    }

    public void setOperacoesVaga(Set<OperacaoVaga> operacoesVaga) {
        if(operacoesVaga != null){
            if(this.operacoesVaga == null) this.operacoesVaga = new HashSet<>();
            else this.operacoesVaga.clear();
            this.operacoesVaga.addAll(operacoesVaga);
        }
    }

    public VagaResponseDTO toResponseDTO() {
        VagaResponseDTO dto = new VagaResponseDTO();
        dto.setId(this.id);
        if (this.endereco != null) {
            dto.setEndereco(this.endereco.toResponseDTO());
        }
        dto.setArea(this.area);
        dto.setNumeroEndereco(this.numeroEndereco);
        dto.setReferenciaEndereco(this.referenciaEndereco);
        dto.setTipoVaga(this.tipoVaga);
        dto.setReferenciaGeoInicio(this.referenciaGeoInicio);
        dto.setReferenciaGeoFim(this.referenciaGeoFim);
        dto.setComprimento(this.comprimento);
        dto.setStatus(this.status);
        if (this.operacoesVaga != null) {
            dto.setOperacoesVaga(
                this.operacoesVaga.stream()
                    .map(OperacaoVaga::toResponseDTO)
                    .collect(Collectors.toSet())
            );
        }
        return dto;
    }
 }