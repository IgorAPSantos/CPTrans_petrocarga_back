package com.cptrans.petrocarga.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.models.DisponibilidadeVaga;
import com.cptrans.petrocarga.repositories.DisponibilidadeVagaRepository;

@Service
public class DisponibilidadeVagaService {

    @Autowired
    private DisponibilidadeVagaRepository disponibilidadeVagaRepository;

    public List<DisponibilidadeVaga> findAll() {
        return disponibilidadeVagaRepository.findAll();
    }

    public Optional<DisponibilidadeVaga> findById(UUID id) {
        return disponibilidadeVagaRepository.findById(id);
    }


    
    public DisponibilidadeVaga save(DisponibilidadeVaga disponibilidadeVaga) {
        return disponibilidadeVagaRepository.save(disponibilidadeVaga);
    }

    public void deleteById(UUID id) {
        disponibilidadeVagaRepository.deleteById(id);
    }
}
