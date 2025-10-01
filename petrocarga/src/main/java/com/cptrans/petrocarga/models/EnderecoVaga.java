package com.cptrans.petrocarga.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "endereco_vaga")
public class EnderecoVaga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100, unique = true)
    @Size(min = 10, max = 100)
    private String rua;

    @Column(nullable = false, length = 50)
    @Size(min = 3, max = 50)
    private String bairro;

    @Column(nullable = false, length = 9, unique = true, name = "codigo_pmp")
    @Size(min = 1, max = 20)
    private String codigoPmp;

    public EnderecoVaga() {}

    public EnderecoVaga(String rua, String bairro, String codigoPmp) {
        this.rua = rua;
        this.bairro = bairro;
        this.codigoPmp = codigoPmp;
    }

    public Integer getId() {
        return id;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCodigoPmp() {
        return codigoPmp;
    }

    public void setCodigoPmp(String codigoPmp) {
        this.codigoPmp = codigoPmp;
    }

    @Override
    public String toString() {
        return "EnderecoVaga{" +
                "id=" + id +
                ", rua='" + rua + '\'' +
                ", bairro='" + bairro + '\'' +
                ", codigoPmp='" + codigoPmp + '\'' +
                '}';
    }
}