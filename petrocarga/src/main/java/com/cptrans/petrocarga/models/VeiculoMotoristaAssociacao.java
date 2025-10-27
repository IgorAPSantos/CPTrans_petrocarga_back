package com.cptrans.petrocarga.models;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "veiculo_motorista_associacao")
public class VeiculoMotoristaAssociacao {

    @EmbeddedId
    private VeiculoMotoristaId id;

    @ManyToOne
    @MapsId("veiculoId")
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    @ManyToOne
    @MapsId("motoristaId")
    @JoinColumn(name = "motorista_id")
    private Motorista motorista;

    // Constructors
    public VeiculoMotoristaAssociacao() {}

    public VeiculoMotoristaAssociacao(Veiculo veiculo, Motorista motorista) {
        this.veiculo = veiculo;
        this.motorista = motorista;
        this.id = new VeiculoMotoristaId(veiculo.getId(), motorista.getId());
    }

    // Getters and Setters
    public VeiculoMotoristaId getId() {
        return id;
    }

    public void setId(VeiculoMotoristaId id) {
        this.id = id;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public Motorista getMotorista() {
        return motorista;
    }

    public void setMotorista(Motorista motorista) {
        this.motorista = motorista;
    }
}
