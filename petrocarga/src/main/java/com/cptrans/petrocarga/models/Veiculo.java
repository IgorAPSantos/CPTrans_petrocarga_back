package com.cptrans.petrocarga.models;

import java.util.UUID;

import com.cptrans.petrocarga.enums.TipoVeiculoEnum;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "veiculo", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"placa", "usuario_id"})
})
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(nullable = false, length = 7, columnDefinition="CHAR(7)")
    private String placa;

    @Column(length = 50)
    private String marca;

    @Column(length = 50)
    private String modelo;

    @Enumerated(EnumType.STRING)
    private TipoVeiculoEnum tipo;

    @Column(precision = 5, scale = 2)
    private Integer comprimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "cpf_proprietario", length = 11)
    private String cpfProprietario;

    @Column(name = "cnpj_proprietario", length = 14)
    private String cnpjProprietario;

    // Constructors
    public Veiculo() {}

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public TipoVeiculoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoVeiculoEnum tipo) {
        this.tipo = tipo;
    }

    public Integer getComprimento() {
        return comprimento;
    }

    public void setComprimento(Integer comprimento) {
        this.comprimento = comprimento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getCpfProprietario() {
        return cpfProprietario;
    }

    public void setCpfProprietario(String cpfProprietario) {
        this.cpfProprietario = cpfProprietario;
    }

    public String getCnpjProprietario() {
        return cnpjProprietario;
    }

    public void setCnpjProprietario(String cnpjProprietario) {
        this.cnpjProprietario = cnpjProprietario;
    }
}
