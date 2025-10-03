package com.cptrans.petrocarga.dto;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.cptrans.petrocarga.enums.DiaSemanaEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public class VagaRequestDTO {

    @Valid
    private EnderecoVagaRequestDTO endereco;

    @Valid
    @Schema(
        description = "Localização da vaga em formato latitude, longitude",
        example = "-22.509135, -43.171351"
        
    )
    @Size(min = 5, max = 100, message="O campo 'localizacao' deve ter entre 5 e 100 caracteres.")
    private String localizacao;
    
    @Valid
    @Schema(
        description = "Horário de inicial de funcionamento da vaga no formato HH:mm",
        example = "00:00"
    )
    private LocalTime horarioInicio;
    
    @Valid
    @Schema(
        description = "Horário final de funcionamento da vaga no formato HH:mm",
        example = "13:00"
    )
    private LocalTime horarioFim;

    @Valid
    @Schema(
        description = "Número máximo de eixos permitidos para a vaga",
        example = "2",
        minimum = "2",
        maximum = "10"
    )
    private Integer maxEixos;
    
    @Valid
    @Schema(
        description = "Comprimento máximo em metros permitido para a vaga",
        example = "12",
        minimum = "5",
        maximum = "30"
    )
    private Integer comprimento;

    @Valid
    @Schema(
        description = "Códigos dos dias da semana (1=Domingo, 2=Segunda, ..., 7=Sábado)",
        example = "[1,2,3]",
        allowableValues = {"1","2","3","4","5","6","7"}
    )
    private Set<Integer> diasSemana;

    public EnderecoVagaRequestDTO getEndereco(){
        return this.endereco;
    }
    
    public LocalTime getHorarioInicio(){
        return this.horarioInicio;
    }
     public LocalTime getHorarioFim(){
        return this.horarioFim;
    }
     public Integer getMaxEixos(){
        return this.maxEixos;
    }
     public Integer getComprimento(){
        return this.comprimento;
    }
     public String getLocalizacao(){
        return this.localizacao;
    }
    public TreeSet<DiaSemanaEnum> getDiasSemana() {
        return this.diasSemana.stream().map(num -> DiaSemanaEnum.toEnum(num)).sorted(Comparator.comparingInt(dia -> dia.codigo)).collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingInt(dia -> dia.codigo))));
    }
}
