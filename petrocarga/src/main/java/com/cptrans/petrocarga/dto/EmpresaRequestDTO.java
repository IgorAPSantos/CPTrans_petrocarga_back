package com.cptrans.petrocarga.dto;

import com.cptrans.petrocarga.models.Empresa;
import com.cptrans.petrocarga.models.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public class EmpresaRequestDTO {

    @NotNull
    private UUID usuarioId;

    @NotBlank
    @Size(min = 14, max = 14)
    private String cnpj;

    @NotBlank
    private String razaoSocial;

    public Empresa toEntity(Usuario usuario) {
        Empresa empresa = new Empresa();
        empresa.setUsuario(usuario);
        empresa.setCnpj(this.cnpj);
        empresa.setRazaoSocial(this.razaoSocial);
        return empresa;
    }

    // Getters and Setters
    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }
}
