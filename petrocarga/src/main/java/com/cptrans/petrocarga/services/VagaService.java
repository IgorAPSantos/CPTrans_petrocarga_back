package com.cptrans.petrocarga.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.repositories.VagaRepository;

@Service
public class VagaService {
    @Autowired
    private VagaRepository vagaRepository;

    public List<Vaga> getVagas() {
        try {
            return vagaRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar vagas: " + e.getMessage());
        }
    }

    // public Vaga createVaga(VagaRequestDTO vagaRequest){
    //     try {
            

    //     } catch (Exception e) {
    //         throw new RuntimeErrorException("Erro ao cadastrar vaga: " + e.getMessage());
    //     }
    // }

    // private Vaga buscarVagaPorLocalizacao(String localizacao){
    //     try {
    //         return vagaRepository.findByLocalizacao(localizacao).get();
    //     } catch (Exception e) {
    //         throw new RuntimeException("Erro ao buscar vaga por ID: " + e.getMessage());
    //     }
    // }
}