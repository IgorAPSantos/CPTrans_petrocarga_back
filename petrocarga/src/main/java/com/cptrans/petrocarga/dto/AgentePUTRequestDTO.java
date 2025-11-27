package com.cptrans.petrocarga.dto;

import org.hibernate.validator.constraints.br.CPF;

import com.cptrans.petrocarga.models.Agente;
import com.cptrans.petrocarga.models.Usuario;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AgentePUTRequestDTO {
    @NotBlank(message="Nome não pode ser vazio.")
    private String nome;

    @NotBlank
    @Valid
    @CPF(message="CPF inválido.")
    private String cpf;

    @Size(min = 10, max = 11, message="Telefone deve conter entre 10 e 11 dígitos.")
    private String telefone;

    @NotBlank
    @Valid
    @Email(message="Email inválido.")
    private String email;

    @NotBlank
    private String matricula;
    
    @Size(min = 6, max = 100, message="Senha deve conter no mínimo 6 caracteres.")
    private String senha;

    public Agente toEntity(){
        Agente agente = new Agente();
        Usuario usuario = new Usuario();
        usuario.setNome(this.nome);
        usuario.setCpf(this.cpf);
        usuario.setTelefone(this.telefone);
        usuario.setEmail(this.email);
        usuario.setSenha(this.senha);
        agente.setUsuario(usuario);
        agente.setMatricula(this.matricula);
        return agente;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getMatricula() {
        return matricula;
    }
}
