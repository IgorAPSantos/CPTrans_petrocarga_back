package com.cptrans.petrocarga.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.models.DisponibilidadeVaga;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.repositories.DisponibilidadeVagaRepository;
import com.cptrans.petrocarga.security.UserAuthenticated;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DisponibilidadeVagaService {

    @Autowired
    private DisponibilidadeVagaRepository disponibilidadeVagaRepository;

    @Autowired
    private VagaService vagaService;

    @Autowired
    private UsuarioService usuarioService;

    public List<DisponibilidadeVaga> findAll() {
        return disponibilidadeVagaRepository.findAll();
    }

    public DisponibilidadeVaga findById(UUID id) {
        return disponibilidadeVagaRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("DisponibilidadeVaga não encontrada."));
    }

    public List<DisponibilidadeVaga> findByVagaId(UUID vagaId) {
        return disponibilidadeVagaRepository.findByVagaId(vagaId);
    }

    public DisponibilidadeVaga createDisponibilidadeVaga(DisponibilidadeVaga novaDisponibilidadeVaga, UUID vagaId) {
        UserAuthenticated usuarioLogado = (UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioService.findById(usuarioLogado.id());
        Vaga vaga = vagaService.findById(vagaId);
        if(!disponibilidadeValida(novaDisponibilidadeVaga, vaga)) throw new IllegalArgumentException("Informações inválidas.");
        novaDisponibilidadeVaga.setVaga(vaga);
        novaDisponibilidadeVaga.setCriadoPor(usuario);

        return disponibilidadeVagaRepository.save(novaDisponibilidadeVaga);
    }

    public DisponibilidadeVaga updateDisponibilidadeVaga(UUID disponibilidaId, DisponibilidadeVaga novaDisponibilidadeVaga, UUID vagaId) {
        UserAuthenticated usuarioLogado = (UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioService.findById(usuarioLogado.id());
        DisponibilidadeVaga disponibilidadeCadastrada = findById(disponibilidaId);
        Vaga vaga = vagaService.findById(vagaId);
        if(!disponibilidadeValida(novaDisponibilidadeVaga, vaga)) throw new IllegalArgumentException("Informações inválidas.");
        disponibilidadeCadastrada.setVaga(vaga);
        disponibilidadeCadastrada.setCriadoPor(usuario);
        return disponibilidadeVagaRepository.save(disponibilidadeCadastrada);
    }

    public void deleteById(UUID id) {
        DisponibilidadeVaga disponibilidadeVaga = findById(id);
        disponibilidadeVagaRepository.deleteById(disponibilidadeVaga.getId());
    }

    public Boolean disponibilidadeValida(DisponibilidadeVaga novaDisponibilidadeVaga, Vaga vaga) {
        if(novaDisponibilidadeVaga.getFim().isBefore(novaDisponibilidadeVaga.getInicio())) {
            throw new IllegalArgumentException("A data de fim deve ser depois da data de inicio.");
        }
        if(novaDisponibilidadeVaga.getFim().equals(novaDisponibilidadeVaga.getInicio())) {
            throw new IllegalArgumentException("A data início e fim devem ser diferentes.");
        }
        List<DisponibilidadeVaga> disponibilidadeVagas = findByVagaId(vaga.getId());
        for (DisponibilidadeVaga disponibilidade : disponibilidadeVagas) {
            if(novaDisponibilidadeVaga.getInicio().toInstant().equals(disponibilidade.getInicio().toInstant()) && novaDisponibilidadeVaga.getFim().toInstant().equals(disponibilidade.getFim().toInstant())) {
                throw new IllegalArgumentException("Já existe uma disponibilidade para a vaga de id: " + vaga.getId() + " nesse horario.");
            }
        }
        return true;
    }
}
