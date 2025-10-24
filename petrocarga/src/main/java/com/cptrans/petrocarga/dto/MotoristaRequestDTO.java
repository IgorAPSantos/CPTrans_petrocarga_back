package com.cptrans.petrocarga.dto;

import com.cptrans.petrocarga.enums.TipoCnhEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public class MotoristaRequestDTO {

    @NotNull
    private UUID usuarioId;

    private TipoCnhEnum tipoCNH;

    @Size(max = 20)
    private String numeroCNH;

    private LocalDate dataValidadeCNH;

    private UUID empresaId;

    // Getters and Setters
    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public TipoCnhEnum getTipoCNH() {
        return tipoCNH;
    }

    public void setTipoCNH(TipoCnhEnum tipoCNH) {
        this.tipoCNH = tipoCNH;
    }

    public String getNumeroCNH() {
        return numeroCNH;
    }

    public void setNumeroCNH(String numeroCNH) {
        this.numeroCNH = numeroCNH;
    }

    public LocalDate getDataValidadeCNH() {
        return dataValidadeCNH;
    }

    public void setDataValidadeCNH(LocalDate dataValidadeCNH) {
        this.dataValidadeCNH = dataValidadeCNH;
    }

    public UUID getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(UUID empresaId) {
        this.empresaId = empresaId;
    }
}
