package com.cptrans.petrocarga.dto;

import com.cptrans.petrocarga.models.Agente;

public class AgenteResponseDTO {

    private UsuarioResponseDTO usuario;
    private String matricula;

    public AgenteResponseDTO() {}

    public AgenteResponseDTO(Agente agente) {
        this.usuario = new UsuarioResponseDTO(agente.getUsuario());
        this.matricula = agente.getMatricula();
    }

    // Getters and Setters
    public UsuarioResponseDTO getUsuario() {
        return usuario;
    }
    public void setUsuario(UsuarioResponseDTO usuario) {
        this.usuario = usuario;
    }
    public String getMatricula() {
        return matricula;
    }
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}
