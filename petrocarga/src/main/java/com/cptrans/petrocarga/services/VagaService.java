package com.cptrans.petrocarga.services;

import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.dto.VagaRequestDTO;
import com.cptrans.petrocarga.models.EnderecoVaga;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.repositories.VagaRepository;

import jakarta.transaction.Transactional;

@Service
public class VagaService {
    @Autowired
    private VagaRepository vagaRepository;
    @Autowired
    private EnderecoVagaService enderecoVagaService;

    public List<Vaga> listarVagas() {
        try {
            return vagaRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar vagas: " + e.getMessage());
        }
    }


    @Transactional()
    public Vaga cadastrarVaga(VagaRequestDTO vagaRequest){
        try {
            EnderecoVaga enderecoVaga = enderecoVagaService.cadastrarEnderecoVaga(vagaRequest.getEndereco());
            Vaga vaga = new Vaga();
            if(vagaRequest.getComprimento() == null) {
                throw new IllegalArgumentException("O campo 'comprimento' é obrigatório e não pode ser nulo ou vazio.");
            }
            if(vagaRepository.findByLocalizacao(vagaRequest.getLocalizacao()).isPresent()) {
                throw new IllegalArgumentException("Já existe uma vaga cadastrada com a localização: " + vagaRequest.getLocalizacao());
            }
            vaga.setEndereco(enderecoVaga);
            vaga.setLocalizacao(vagaRequest.getLocalizacao());
            vaga.setHorarioInicio(vagaRequest.getHorarioInicio());
            vaga.setHorarioFim(vagaRequest.getHorarioFim());
            vaga.setMaxEixos(vagaRequest.getMaxEixos());
            vaga.setComprimento(vagaRequest.getComprimento());
            vaga.setDiasSemana(vagaRequest.getDiasSemana());
            return vagaRepository.save(vaga);

        } catch (RuntimeErrorException e) {
            throw new RuntimeException("Erro ao cadastrar vaga: " + e.getMessage());
        }
    }

    // private Vaga buscarVagaPorLocalizacao(String localizacao){
    //     try {
    //         return vagaRepository.findByLocalizacao(localizacao).get();
    //     } catch (Exception e) {
    //         throw new RuntimeException("Erro ao buscar vaga por ID: " + e.getMessage());
    //     }
    // }
}