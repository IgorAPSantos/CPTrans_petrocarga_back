package com.cptrans.petrocarga.models;

import com.cptrans.petrocarga.enums.TipoVeiculoEnum;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "Veiculo", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"placa", "usuario_id"})
})
// Note: The CHECK constraint 'chk_cpf_cnpj_proprietario' (cpf_proprietario IS NULL AND cnpj_proprietario IS NOT NULL) OR (cpf_proprietario IS NOT NULL AND cnpj_proprietario IS NULL)
// needs to be handled either by a custom validator or by database-level constraint.
// JPA @Check annotation is not widely supported across all JPA providers for complex expressions.
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "UniqueID")
    private UUID id;

    @Column(nullable = false, length = 10)
    private String placa;

    @Column(length = 100)
    private String marca;

    @Column(length = 100)
    private String modelo;

    @Enumerated(EnumType.STRING)
    private TipoVeiculoEnum tipo;

    @Column(precision = 5, scale = 2)
    private BigDecimal comprimento;

    @ManyToOne
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

    public BigDecimal getComprimento() {
        return comprimento;
    }

    public void setComprimento(BigDecimal comprimento) {
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
