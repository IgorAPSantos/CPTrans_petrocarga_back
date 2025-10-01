package com.cptrans.petrocarga.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "dia_semana")
public class DiaSemana {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique=true)
    private String dia;

    @ManyToMany(mappedBy="diasSemana")
    private Set<Vaga> vagas = new HashSet<>();
    public DiaSemana() {}

    public DiaSemana(String dia) {
        this.dia = dia;
    }

    public Integer getId() {
        return id;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public Set<Vaga> getVagas() {
        return vagas;
    }

    public void setVagas(Set<Vaga> vagas) {
        this.vagas = vagas;
    }

    @Override
    public String toString() {
        return "DiaSemana [id=" + id + ", dia=" + dia + ", vagas=" + vagas + "]";
    }
}
