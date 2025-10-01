package com.cptrans.petrocarga.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.models.EnderecoVaga;
import com.cptrans.petrocarga.repositories.EnderecoVagaRepository;

@Service
public class EnderecoVagaService {
    
    @Autowired
    private EnderecoVagaRepository enderecoVagaRepository;

    public EnderecoVaga findEnderecoByCodigo_pmp(String codigoPmp){
        try {
             Optional<EnderecoVaga> enderecoVaga = enderecoVagaRepository.findByCodigoPmp(codigoPmp);
            if(enderecoVaga.isPresent()){
                return enderecoVaga.get();
            } else {
                throw new RuntimeException("Endereço não encontrado para o código PMP: " + codigoPmp);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao buscar endereço por código PMP: " + e.getMessage());
        }
    }
}
