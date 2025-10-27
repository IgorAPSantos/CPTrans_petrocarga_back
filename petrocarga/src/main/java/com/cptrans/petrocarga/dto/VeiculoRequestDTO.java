package com.cptrans.petrocarga.dto;

import java.util.UUID;

import com.cptrans.petrocarga.enums.TipoVeiculoEnum;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Veiculo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VeiculoRequestDTO {

    @NotBlank
    @Size(max = 10)
    private String placa;

    private String marca;

    private String modelo;

    private TipoVeiculoEnum tipo;

    private Integer comprimento;

    @NotNull
    private UUID usuarioId;

    private String cpfProprietario;

    private String cnpjProprietario;

    public Veiculo toEntity(Usuario usuario) {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(this.placa);
        veiculo.setMarca(this.marca);
        veiculo.setModelo(this.modelo);
        veiculo.setTipo(this.tipo);
        veiculo.setComprimento(this.comprimento);
        veiculo.setUsuario(usuario);
        veiculo.setCpfProprietario(this.cpfProprietario);
        veiculo.setCnpjProprietario(this.cnpjProprietario);
        return veiculo;
    }

    // Getters and Setters
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

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
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
