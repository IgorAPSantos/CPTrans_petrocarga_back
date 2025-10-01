package com.cptrans.petrocarga.models;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import com.cptrans.petrocarga.enums.AreaVagaEnum;
import com.cptrans.petrocarga.enums.StatusVagaEnum;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "vaga")
public class Vaga {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
    private Integer max_eixos;

    @Column(nullable=false)
    private Integer comprimento;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusVagaEnum status;

    @ManyToMany
    @JoinTable(
    name = "dia_vaga",
    joinColumns = @JoinColumn(name = "vaga_id"),
    inverseJoinColumns = @JoinColumn(name = "dia_semana_id")
    )
    private Set<DiaSemana> diasSemana = new HashSet<>();

    public Vaga() {
        this.area = AreaVagaEnum.AMARELA;
        this.status = StatusVagaEnum.DISPONIVEL;
    }

    public Vaga(EnderecoVaga endereco, AreaVagaEnum area, String localizacao,
            LocalTime horarioInicio, LocalTime horarioFim, Integer max_eixos, Integer comprimento, StatusVagaEnum status, Set<DiaSemana> diasSemana) {
        this.endereco = endereco;
        this.area = area != null ? area : AreaVagaEnum.AMARELA;
        this.localizacao = localizacao;
        this.horarioInicio = horarioInicio != null ? horarioInicio : LocalTime.of(0, 0, 0, 0);
        this.horarioFim = horarioFim != null ? horarioFim : LocalTime.of(13, 0, 0, 0);
        this.max_eixos = max_eixos != null ? max_eixos : 2;
        this.comprimento = comprimento != null ? comprimento : 5;
        this.status = status != null ? status : StatusVagaEnum.DISPONIVEL;
        this.diasSemana = diasSemana != null ? diasSemana : new HashSet<>();
    }

    public Integer getId() {
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
        this.horarioInicio = horarioInicio;
    }

    public LocalTime getHorarioFim() {
        return horarioFim;
    }

    public void setHorario_fim(LocalTime horarioFim) {
        this.horarioFim = horarioFim;
    }

    public Integer getMax_eixos() {
        return max_eixos;
    }

    public void setMax_eixos(Integer max_eixos) {
        this.max_eixos = max_eixos;
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

    public Set<DiaSemana> getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(Set<DiaSemana> diasSemana) {
        this.diasSemana = diasSemana;
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
                ", max_eixos=" + max_eixos +
                ", comprimento=" + comprimento +
                ", status=" + status +
                ", diasSemana=" + diasSemana +
                '}';
    }
}   

