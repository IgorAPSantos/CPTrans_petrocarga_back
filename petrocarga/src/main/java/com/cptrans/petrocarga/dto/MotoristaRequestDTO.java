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
    private TipoCnhEnum tipoCNH;

    @Size(min = 9, max = 9, message = "Número da CNH deve ter exatamente 9 caracteres.")
    private String numeroCNH;

    @NonNull
    @Valid
    private LocalDate dataValidadeCNH;

    @Valid
    private UUID empresaId;

    public Motorista toEntity(Empresa empresa) {
        Motorista motorista = new Motorista();
        if (empresa != null){
            motorista.setEmpresa(empresa);
        }
        motorista.setUsuario(this.usuario.toEntity());
        motorista.setTipoCNH(this.tipoCNH);
        motorista.setNumeroCNH(this.numeroCNH);
        motorista.setDataValidadeCNH(this.dataValidadeCNH);
        return motorista;
    }

    // Getters and Setters
    public UsuarioRequestDTO getUsuario() {
        return usuario;
    }
    public TipoCnhEnum getTipoCNH() {
        return tipoCNH;
    }
    public String getNumeroCNH() {
        return numeroCNH;
    }
    public LocalDate getDataValidadeCNH() {
        return dataValidadeCNH;
    }
    public UUID getEmpresaId() {
        return empresaId;
    }
}
