package com.cptrans.petrocarga.dto;

public record AgenteFiltrosDTO(
    String nome,
    String cpf,
    String matricula,
    Boolean ativo,
    String email
) {
    
}
