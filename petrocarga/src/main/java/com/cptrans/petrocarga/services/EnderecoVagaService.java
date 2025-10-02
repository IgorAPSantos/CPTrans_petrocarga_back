package com.cptrans.petrocarga.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.dto.EnderecoVagaRequestDTO;
import com.cptrans.petrocarga.models.EnderecoVaga;
import com.cptrans.petrocarga.repositories.EnderecoVagaRepository;

@Service
public class EnderecoVagaService {
    
    @Autowired
    private EnderecoVagaRepository enderecoVagaRepository;

    public EnderecoVaga cadastrarEnderecoVaga(EnderecoVagaRequestDTO enderecoVagaRequest){
        try {
            if(enderecoVagaRequest == null) {
                throw new IllegalArgumentException("O objeto EnderecoVagaRequestDTO não pode ser nulo.");
            }
            EnderecoVaga enderecoVagaCadastrado = findEnderecoByCodigo_pmp(enderecoVagaRequest.getCodigoPMP());
            if(enderecoVagaCadastrado != null) {
                return enderecoVagaCadastrado;
            }
            if(enderecoVagaRequest.getCodigoPMP() == null || enderecoVagaRequest.getCodigoPMP().isEmpty()) {
                throw new IllegalArgumentException("O campo 'codigoPMP' é obrigatório e não pode ser nulo ou vazio.");
            }
            if(enderecoVagaRequest.getLogradouro() == null || enderecoVagaRequest.getLogradouro().isEmpty()) {
                throw new IllegalArgumentException("O campo 'logradouro' é obrigatório e não pode ser nulo ou vazio.");
            }
            if(enderecoVagaRequest.getBairro() == null || enderecoVagaRequest.getBairro().isEmpty()) {
                throw new IllegalArgumentException("O campo 'bairro' é obrigatório e não pode ser nulo ou vazio.");
            }
            if(enderecoVagaRepository.findByCodigoPmp(enderecoVagaRequest.getCodigoPMP()).isPresent()) {
                throw new IllegalArgumentException("Já existe um endereço cadastrado com o código PMP: " + enderecoVagaRequest.getCodigoPMP());
            }
            EnderecoVaga enderecoVaga = new EnderecoVaga();
            enderecoVaga.setCodigoPmp(enderecoVagaRequest.getCodigoPMP());
            enderecoVaga.setLogradouro(enderecoVagaRequest.getLogradouro());
            enderecoVaga.setBairro(enderecoVagaRequest.getBairro());
            return enderecoVagaRepository.save(enderecoVaga);
        } catch (RuntimeException e) {
            throw new RuntimeException("Erro ao cadastrar endereço: " + e.getMessage());
        }
    }

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
