package com.cptrans.petrocarga.services;

import com.cptrans.petrocarga.models.DisponibilidadeVaga;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.repositories.DisponibilidadeVagaRepository;
import com.cptrans.petrocarga.repositories.VagaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DisponibilidadeVagaService {

    private final VagaRepository vagaRepository;

    @Autowired
    private DisponibilidadeVagaRepository disponibilidadeVagaRepository;

    DisponibilidadeVagaService(VagaRepository vagaRepository) {
        this.vagaRepository = vagaRepository;
    }

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
