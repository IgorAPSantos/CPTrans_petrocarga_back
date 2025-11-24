package com.cptrans.petrocarga.models;

import java.time.LocalDate;
import java.util.UUID;

import com.cptrans.petrocarga.dto.MotoristaResponseDTO;
import com.cptrans.petrocarga.enums.TipoCnhEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "motorista")
public class Motorista {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cnh", length=2, nullable = false)
    private TipoCnhEnum tipoCnh;

    @Column(name = "numero_cnh", unique = true, length = 9, nullable = false)
    private String numeroCnh;

    @Column(name = "data_validade_cnh")
    private LocalDate dataValidadeCnh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    // Constructors
    public Motorista() {}

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public TipoCnhEnum getTipoCNH() {
        return tipoCnh;
    }

    public void setTipoCNH(TipoCnhEnum tipoCnh) {
        this.tipoCnh = tipoCnh;
    }

    public String getNumeroCNH() {
        return numeroCnh;
    }

    public void setNumeroCNH(String numeroCnh) {
        this.numeroCnh = numeroCnh;
    }

    public LocalDate getDataValidadeCNH() {
        return dataValidadeCnh;
    }

    public void setDataValidadeCNH(LocalDate dataValidadeCnh) {
        this.dataValidadeCnh = dataValidadeCnh;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public MotoristaResponseDTO toResponseDTO() {
        return new MotoristaResponseDTO(this);
    }
}
