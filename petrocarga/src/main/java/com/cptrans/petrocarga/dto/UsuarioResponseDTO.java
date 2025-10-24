package com.cptrans.petrocarga.dto;

import com.cptrans.petrocarga.enums.PermissoesEnum;
import com.cptrans.petrocarga.models.Usuario;
import java.time.OffsetDateTime;
import java.util.UUID;

public class UsuarioResponseDTO {

    private UUID id;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private PermissoesEnum permissao;
    private OffsetDateTime criadoEm;
    private Boolean ativo;
    private OffsetDateTime desativadoEm;

    public UsuarioResponseDTO() {
    }

    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.cpf = usuario.getCpf();
        this.telefone = usuario.getTelefone();
        this.email = usuario.getEmail();
        this.permissao = usuario.getPermissao();
        this.criadoEm = usuario.getCriadoEm();
        this.ativo = usuario.getAtivo();
        this.desativadoEm = usuario.getDesativadoEm();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PermissoesEnum getPermissao() {
        return permissao;
    }

    public void setPermissao(PermissoesEnum permissao) {
        this.permissao = permissao;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public OffsetDateTime getDesativadoEm() {
        return desativadoEm;
    }

    public void setDesativadoEm(OffsetDateTime desativadoEm) {
        this.desativadoEm = desativadoEm;
    }
}
