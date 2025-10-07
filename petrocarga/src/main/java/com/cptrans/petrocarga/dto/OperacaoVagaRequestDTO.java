package com.cptrans.petrocarga.dto;

import java.time.LocalTime;
import com.cptrans.petrocarga.enums.DiaSemanaEnum;

public class OperacaoVagaRequestDTO {
    
    private DiaSemanaEnum diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    // Getters e Setters
    public DiaSemanaEnum getDiaSemana() {
        return diaSemana;
    }
    public void setDiaSemana(DiaSemanaEnum diaSemana) {
        this.diaSemana = diaSemana;
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