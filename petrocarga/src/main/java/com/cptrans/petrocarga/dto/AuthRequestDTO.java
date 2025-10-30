package com.cptrans.petrocarga.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class AuthRequestDTO {
    
    @Valid
    @Email(message = "Informe um email válido.")
    public String email;

    @Valid
    @Size(min = 6, message = "Senha deve conter no mínimo 6 caracteres.")
    public String senha;

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }
}
