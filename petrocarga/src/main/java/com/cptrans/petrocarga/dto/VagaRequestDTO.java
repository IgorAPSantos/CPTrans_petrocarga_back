package com.cptrans.petrocarga.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class VagaRequestDTO {
    @Size(min = 3, max = 50, message="O campo 'codigoPMP' deve ter entre 3 e 50 caracteres.")
    private String codigoPMP;

    @Size(min = 3, max = 100, message="O campo 'logradouro' deve ter entre 3 e 100 caracteres.")
    private String logradouro;

    @Size(min = 3, max = 50, message="O campo 'bairro' deve ter entre 3 e 50 caracteres.")
    private String bairro;
    
    @Size(min = 5, max = 100, message="O campo 'localizacao' deve ter entre 5 e 100 caracteres.")
    private String localizacao;
    
    @NotEmpty(message="O campo 'horario_incio' não pode ser vazio.")
    private LocalTime horario_inicio;
    
    @NotEmpty(message="O campo 'horario_fim' não pode ser vazio.")
    private LocalTime horario_fim;
    
    @NotEmpty(message="O campo 'max_eixos' não pode ser vazio.")
    private Integer max_eixos;
    
    @NotEmpty(message="O campo 'comprimento' não pode ser vazio.")
    private Integer comprimento;
    
    public String getCodigoPMP(){
        return this.codigoPMP;
    }
    public String getLogradouro(){
        return this.logradouro;
    }
    public String getBairro(){
        return this.bairro;
    }
    public LocalTime getHorario_inicio(){
        return this.horario_inicio;
    }
     public LocalTime getHorario_fim(){
        return this.horario_fim;
    }
     public Integer getMax_eixos(){
        return this.max_eixos;
    }
     public Integer getComprimento(){
        return this.comprimento;
    }
     public String getLocalizacao(){
        return this.localizacao;
    }
}
