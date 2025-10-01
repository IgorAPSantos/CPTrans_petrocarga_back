package com.cptrans.petrocarga.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.models.DiaSemana;
import com.cptrans.petrocarga.repositories.DiaSemanaRepository;

@Service
public class DiaSemanaService {
    
    @Autowired
    private DiaSemanaRepository diaSemanaRepository;

    public void createDiaSemana() {
        try {
            List<DiaSemana> dias = List.of(
                new DiaSemana("Domingo"),
                new DiaSemana("Segunda-feira"),
                new DiaSemana("Terça-feira"),
                new DiaSemana("Quarta-feira"),
                new DiaSemana("Quinta-feira"),
                new DiaSemana("Sexta-feira"),
                new DiaSemana("Sábado")
            );

            dias.forEach(dia -> {diaSemanaRepository.save(dia);});
        }catch (Exception e) {
            throw new RuntimeException("Erro ao criar dias da semana: " + e.getMessage());
        }
    }
}
