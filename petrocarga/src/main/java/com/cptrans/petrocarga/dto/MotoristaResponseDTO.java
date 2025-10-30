package com.cptrans.petrocarga.dto;

import com.cptrans.petrocarga.enums.TipoCnhEnum;
import com.cptrans.petrocarga.models.Motorista;

import java.time.LocalDate;
import java.util.UUID;

public class MotoristaResponseDTO {

    private UUID id;
    private UsuarioResponseDTO usuario;
    private TipoCnhEnum tipoCNH;
    private String numeroCNH;
    private LocalDate dataValidadeCNH;
    private UUID empresaId;

    public MotoristaResponseDTO() {
    }

    public MotoristaResponseDTO(Motorista motorista) {
        this.id = motorista.getId();
        this.usuario = new UsuarioResponseDTO(motorista.getUsuario());
        this.tipoCNH = motorista.getTipoCNH();
        this.numeroCNH = motorista.getNumeroCNH();
        this.dataValidadeCNH = motorista.getDataValidadeCNH();
        if (motorista.getEmpresa() != null) {
            this.empresaId = motorista.getEmpresa().getId();
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UsuarioResponseDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResponseDTO usuario) {
        this.usuario = usuario;
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
