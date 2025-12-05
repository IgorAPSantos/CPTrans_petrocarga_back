package com.cptrans.petrocarga.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.cptrans.petrocarga.enums.TipoCnhEnum;
import com.cptrans.petrocarga.models.Empresa;
import com.cptrans.petrocarga.models.Motorista;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public class MotoristaRequestDTO {

    @NonNull
    @Valid
    private UsuarioRequestDTO usuario;

    @NonNull
    @Valid
    private TipoCnhEnum tipoCnh;

    @Size(min = 9, max = 9, message = "Número da CNH deve ter exatamente 9 caracteres.")
    private String numeroCnh;

    @NonNull
    @Valid
    private LocalDate dataValidadeCnh;

    @Valid
    private UUID empresaId;

    public Motorista toEntity(Empresa empresa) {
        Motorista motorista = new Motorista();
        if (empresa != null){
            motorista.setEmpresa(empresa);
        }
        motorista.setUsuario(this.usuario.toEntity());
        motorista.setTipoCnh(this.tipoCnh);
        motorista.setNumeroCnh(this.numeroCnh);
        motorista.setDataValidadeCnh(this.dataValidadeCnh);
        return motorista;
    }

    // Getters and Setters
    public UsuarioRequestDTO getUsuario() {
        return usuario;
    }
    public TipoCnhEnum getTipoCnh() {
        return tipoCnh;
    }
    public String getNumeroCnh() {
        return numeroCnh;
    }
    public LocalDate getDataValidadeCnh() {
        return dataValidadeCnh;
    }
    public UUID getEmpresaId() {
        return empresaId;
    }
}
