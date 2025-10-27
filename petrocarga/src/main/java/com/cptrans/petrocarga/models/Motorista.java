package com.cptrans.petrocarga.models;

import com.cptrans.petrocarga.enums.TipoCnhEnum;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "Motorista")
public class Motorista {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "UniqueID")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_CNH")
    private TipoCnhEnum tipoCNH;

    @Column(name = "numero_CNH", unique = true, length = 20)
    private String numeroCNH;

    @Column(name = "data_validade_CNH")
    private LocalDate dataValidadeCNH;

    @ManyToOne
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

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }
}
