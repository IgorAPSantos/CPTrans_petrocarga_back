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
        if(enderecoVagaRequest == null) {
            throw new IllegalArgumentException("O objeto EnderecoVagaRequestDTO não pode ser nulo.");
        }
        Optional<EnderecoVaga> enderecoCadrastado = enderecoVagaRepository.findByCodigoPmp(enderecoVagaRequest.getCodigoPmp());
        if(enderecoCadrastado.isPresent()){
            return enderecoCadrastado.get();
        }
        if(enderecoVagaRequest.getCodigoPmp() == null || enderecoVagaRequest.getCodigoPmp().isEmpty()) {
            throw new IllegalArgumentException("O campo 'codigoPMP' é obrigatório e não pode ser nulo ou vazio.");
        }
        if(enderecoVagaRequest.getLogradouro() == null || enderecoVagaRequest.getLogradouro().isEmpty()) {
            throw new IllegalArgumentException("O campo 'logradouro' é obrigatório e não pode ser nulo ou vazio.");
        }
        if(enderecoVagaRequest.getBairro() == null || enderecoVagaRequest.getBairro().isEmpty()) {
            throw new IllegalArgumentException("O campo 'bairro' é obrigatório e não pode ser nulo ou vazio.");
        }
        EnderecoVaga enderecoVaga = new EnderecoVaga();
        enderecoVaga.setCodigoPmp(enderecoVagaRequest.getCodigoPmp());
        enderecoVaga.setLogradouro(enderecoVagaRequest.getLogradouro());
        enderecoVaga.setBairro(enderecoVagaRequest.getBairro());
        return enderecoVagaRepository.save(enderecoVaga);
    }

}
