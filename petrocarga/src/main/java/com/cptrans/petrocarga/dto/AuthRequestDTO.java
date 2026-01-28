package com.cptrans.petrocarga.dto;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuthRequestDTO {
    
    @Valid
    @Email(message = "Informe um email válido.")
    public String email;

    @Valid
    @CPF(message = "Informe um CPF válido.")
    public String cpf;

    @Valid
    @NotNull(message = "O campo 'senha' é obrigatório.")
    @Size(min = 6, message = "Senha deve conter no mínimo 6 caracteres.")
    public String senha;

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getCpf() {
        return cpf;
    }
}
