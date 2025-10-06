package com.cptrans.petrocarga.models;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import com.cptrans.petrocarga.dto.VagaResponseDTO;
import com.cptrans.petrocarga.enums.AreaVagaEnum;
import com.cptrans.petrocarga.enums.DiaSemanaEnum;
import com.cptrans.petrocarga.enums.StatusVagaEnum;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

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

    @Column(nullable = false, unique=true)
    @Size(min = 3, max = 50)
    private String localizacao;

    @Column(nullable = false, name="horario_inicio")
    private LocalTime horarioInicio;

    @Column(nullable = false, name="horario_fim")
    private LocalTime horarioFim;

    @Column(nullable = false)
    private Integer maxEixos;

    @Column(nullable=false)
    private Integer comprimento;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusVagaEnum status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "vaga_dias_semana", joinColumns = @JoinColumn(name = "vaga_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private Set<DiaSemanaEnum> diasSemana = new HashSet<>();

    public Vaga() {
        this.area = AreaVagaEnum.AMARELA;
        this.status = StatusVagaEnum.DISPONIVEL;
        this.horarioInicio = LocalTime.of(0, 0, 0, 0);
        this.horarioFim = LocalTime.of(13, 0, 0, 0);
        this.maxEixos = 2;
    }

    public Vaga(EnderecoVaga endereco, AreaVagaEnum area, String localizacao,
            LocalTime horarioInicio, LocalTime horarioFim, Integer maxEixos, Integer comprimento, StatusVagaEnum status, Set<DiaSemanaEnum> diasSemana) {
        this.endereco = endereco;
        this.area = area == null ? AreaVagaEnum.AMARELA : area ;
        this.localizacao = localizacao;
        this.horarioInicio = horarioInicio == null ? LocalTime.of(0, 0, 0, 0) : horarioInicio;
        this.horarioFim = horarioFim == null ? LocalTime.of(13, 0, 0, 0) : horarioFim;
        this.maxEixos = maxEixos == null ? 2 : maxEixos;
        this.comprimento = comprimento == null ? 5 : comprimento;
        this.status = status == null ? status : StatusVagaEnum.DISPONIVEL;
        this.diasSemana = diasSemana;
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
       this.area = area == null ? AreaVagaEnum.AMARELA : area;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        LocalTime horarioInicioPadrao = LocalTime.of(0, 0, 0, 0);
        this.horarioInicio = horarioInicio == null ? horarioInicioPadrao : horarioInicio;
    }

    public LocalTime getHorarioFim() {
        return horarioFim;
    }

    public void setHorarioFim(LocalTime horarioFim) {
        LocalTime horarioFimPadrao = LocalTime.of(13, 0, 0, 0);
        this.horarioFim = horarioFim == null ? horarioFimPadrao : horarioFim;
    }

    public Integer getMaxEixos() {
        return maxEixos;
    }

    public void setMaxEixos(Integer maxEixos) {
        this.maxEixos = maxEixos == null || maxEixos <= 0 ? 2 : maxEixos;
    }

    public Integer getComprimento() {
        return comprimento;
    }

    public void setComprimento(Integer comprimento) {
        this.comprimento = comprimento == null || comprimento <= 0 ? 5 : comprimento;
    }

    public StatusVagaEnum getStatus() {
        return status;
    }

    public void setStatus(StatusVagaEnum status) {
        this.status = status == null ? StatusVagaEnum.DISPONIVEL : status;
    }

    public Set<DiaSemanaEnum> getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(Set<DiaSemanaEnum> diasSemana) { 
        if(diasSemana == null || diasSemana.isEmpty()) {
            Set<DiaSemanaEnum> diasSemanaPadrao = new TreeSet<>(Comparator.comparingInt(dia -> dia.codigo));
            diasSemanaPadrao.add(DiaSemanaEnum.DOMINGO);
            diasSemanaPadrao.add(DiaSemanaEnum.SEGUNDA);
            diasSemanaPadrao.add(DiaSemanaEnum.TERCA);
            diasSemanaPadrao.add(DiaSemanaEnum.QUARTA);
            diasSemanaPadrao.add(DiaSemanaEnum.QUINTA);
            diasSemanaPadrao.add(DiaSemanaEnum.SEXTA);
            diasSemanaPadrao.add(DiaSemanaEnum.SABADO);
            this.diasSemana = diasSemanaPadrao;    
        }else{
            this.diasSemana = diasSemana;
        }
    }

    @Override
    public String toString() {
        return "Vaga{" +
                "id=" + id +
                ", endereco=" + endereco +
                ", area=" + area +
                ", localizacao='" + localizacao + '\'' +
                ", horarioInicio=" + horarioInicio +
                ", horarioFim=" + horarioFim +
                ", maxEixos=" + maxEixos +
                ", comprimento=" + comprimento +
                ", status=" + status +
                ", diasSemana=" + diasSemana +
                '}';
    }

    public VagaResponseDTO toResponseDTO() {
        VagaResponseDTO dto = new VagaResponseDTO();
        dto.setId(this.id);
        dto.setEndereco(this.endereco);
        dto.setArea(this.area);
        dto.setLocalizacao(this.localizacao);
        dto.setHorarioInicio(this.horarioInicio);
        dto.setHorarioFim(this.horarioFim);
        dto.setMaxEixos(this.maxEixos);
        dto.setComprimento(this.comprimento);
        dto.setStatus(this.status);
        dto.setDiasSemana(this.diasSemana);
        
        return dto;
    }
}   

