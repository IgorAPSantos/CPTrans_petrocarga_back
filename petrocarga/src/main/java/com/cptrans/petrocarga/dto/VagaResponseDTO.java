package com.cptrans.petrocarga.dto;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import com.cptrans.petrocarga.enums.AreaVagaEnum;
import com.cptrans.petrocarga.enums.DiaSemanaEnum;
import com.cptrans.petrocarga.enums.StatusVagaEnum;
import com.cptrans.petrocarga.models.EnderecoVaga;

public class VagaResponseDTO {
    private UUID id;
    private EnderecoVagaResponseDTO endereco;
    private AreaVagaEnum area;
    private String localizacao;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private Integer maxEixos;
    private Integer comprimento;
    private StatusVagaEnum status;
    private Set<DiaSemanaEnum> diasSemana;

    public VagaResponseDTO() {
        this.diasSemana = new TreeSet<>(Comparator.comparingInt(dia -> dia.codigo));
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public EnderecoVagaResponseDTO getEnderecoVagaResponseDTO() {
        return endereco;
    }
    public void setEndereco(EnderecoVaga endereco) {
        this.endereco = endereco.toResponseDTO();
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
    public void setHorarioFim(LocalTime horarioFim) {
        this.horarioFim = horarioFim;
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
    public Set<DiaSemanaEnum> getDiasSemana() {
        return diasSemana;
    }
    public void setDiasSemana(Set<DiaSemanaEnum> diasSemana) {
        this.diasSemana = diasSemana.stream().sorted(Comparator.comparingInt(dia -> dia.codigo)).collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingInt(dia -> dia.codigo))));
    }
}
