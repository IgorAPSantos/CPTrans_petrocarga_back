package com.cptrans.petrocarga.services;

import com.cptrans.petrocarga.models.Agente;
import com.cptrans.petrocarga.repositories.AgenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AgenteService {

    @Autowired
    private AgenteRepository agenteRepository;

    public List<Agente> findAll() {
        return agenteRepository.findAll();
    }

    public Optional<Agente> findById(UUID id) {
        return agenteRepository.findById(id);
    }

    public Agente save(Agente agente) {
        return agenteRepository.save(agente);
    }

    public void deleteById(UUID id) {
        agenteRepository.deleteById(id);
    }
}
