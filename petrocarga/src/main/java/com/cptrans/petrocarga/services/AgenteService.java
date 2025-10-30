package com.cptrans.petrocarga.services;

import com.cptrans.petrocarga.models.Agente;
import com.cptrans.petrocarga.repositories.AgenteRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.models.Usuario;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AgenteService {

    @Autowired
    private AgenteRepository agenteRepository;

    @Autowired
    private UsuarioService usuarioService;

    public List<Agente> findAll() {
       return agenteRepository.findAll();
    }

    public Agente findByUsuarioId(UUID usuarioId) {
        Usuario usuario = usuarioService.findById(usuarioId);
        return agenteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new EntityNotFoundException("Agente não encontrado"));
    }

    @Transactional
    public Agente createAgente(Agente agente) {
        if(agenteRepository.existsByMatricula(agente.getMatricula())) {
            throw new IllegalArgumentException("Matrícula já cadastrada");
        }
        Usuario usuario = usuarioService.createUsuario(agente.getUsuario(), PermissaoEnum.AGENTE);
        Agente novoAgente = new Agente();
        novoAgente.setUsuario(usuario);
        novoAgente.setMatricula(agente.getMatricula());
        return agenteRepository.save(novoAgente);
    }

    @Transactional
    public Agente updateAgente(UUID usuarioId, Agente novoAgente) {
        Agente agenteCadastrado = findByUsuarioId(usuarioId);
        Optional<Agente> agenteByMatricula = agenteRepository.findByMatricula(novoAgente.getMatricula());
        
        if(agenteByMatricula.isPresent() && !agenteByMatricula.get().getId().equals(agenteCadastrado.getId())) {
            throw new IllegalArgumentException("Matrícula já cadastrada");
        }
    
        Usuario novoUsuario = usuarioService.updateUsuario(usuarioId, novoAgente.getUsuario(), PermissaoEnum.AGENTE);

        agenteCadastrado.setUsuario(novoUsuario);
        agenteCadastrado.setMatricula(novoAgente.getMatricula());

        return agenteRepository.save(agenteCadastrado);
    }

    public void deleteByUsuarioId(UUID usuarioId) {
        Usuario usuario = usuarioService.findById(usuarioId);
        Agente agente = agenteRepository.findByUsuario(usuario).orElseThrow(() -> new EntityNotFoundException("Agente não encontrado"));
        agenteRepository.deleteById(agente.getId());
    }
}
