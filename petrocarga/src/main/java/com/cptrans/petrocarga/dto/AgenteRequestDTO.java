package com.cptrans.petrocarga.dto;

import com.cptrans.petrocarga.models.Agente;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public class AgenteRequestDTO {

    @NonNull
    @Valid
    private UsuarioRequestDTO usuario;

    @NotBlank
    private String matricula;

    // Getters and Setters
    public UsuarioRequestDTO getUsuario() {
        return usuario;
    }
    public String getMatricula() {
        return matricula;
    }
    
    public Agente toEntity() {
        Agente agente = new Agente();
        agente.setUsuario(this.usuario.toEntity());
        agente.setMatricula(this.matricula);
        return agente;
    }
}
