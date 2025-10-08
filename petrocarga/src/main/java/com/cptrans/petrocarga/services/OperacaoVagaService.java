package com.cptrans.petrocarga.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.models.OperacaoVaga;
import com.cptrans.petrocarga.repositories.OperacaoVagaRepository;

@Service
public class OperacaoVagaService {

    @Autowired
    private OperacaoVagaRepository operacaoVagaRepository;

    public void salvarOperacaoVaga(Set<OperacaoVaga> listaOperacaoVaga) {
        operacaoVagaRepository.saveAll(listaOperacaoVaga);
    }
}
