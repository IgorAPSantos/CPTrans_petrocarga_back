package com.cptrans.petrocarga.dto;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;

public class OperacaoVagaRequestDTO {
    @Schema(description = "Código do dia da semana (do 1 ao 7)", example = "1")
    private Integer codigoDiaSemana;
    @Schema(description = "Hora de inicio", example = "00:00")
    private LocalTime horaInicio;
    @Schema(description = "Hora de fim", example = "13:00")
    private LocalTime horaFim;

    // Getters e Setters
    public Integer getCodigoDiaSemana() {
        return codigoDiaSemana;
    }
    public void setCodigoDiaSemana(Integer codigoDiaSemana) {
        this.codigoDiaSemana = codigoDiaSemana;
    }
    public LocalTime getHoraInicio() {
        return horaInicio;
    }
    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }
    public LocalTime getHoraFim() {
        return horaFim;
    }
    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }
}