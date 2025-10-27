package com.cptrans.petrocarga.controllers;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptrans.petrocarga.dto.DisponibilidadeVagaRequestDTO;
import com.cptrans.petrocarga.dto.DisponibilidadeVagaResponseDTO;
import com.cptrans.petrocarga.models.DisponibilidadeVaga;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.services.DisponibilidadeVagaService;
import com.cptrans.petrocarga.services.UsuarioService;
import com.cptrans.petrocarga.services.VagaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/disponibilidade-vagas")
public class DisponibilidadeVagaController {

    @Autowired
    private DisponibilidadeVagaService disponibilidadeVagaService;

    @Autowired
    private VagaService vagaService;

    @Autowired
    private UsuarioService usuarioService;

    
    @GetMapping
    public ResponseEntity<List<DisponibilidadeVagaResponseDTO>> getAllDisponibilidadeVagas() {
        List<DisponibilidadeVagaResponseDTO> disponibilidadeVagas = disponibilidadeVagaService.findAll().stream()
                .map(DisponibilidadeVagaResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(disponibilidadeVagas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadeVagaResponseDTO> getDisponibilidadeVagaById(@PathVariable UUID id) {
        return disponibilidadeVagaService.findById(id)
                .map(DisponibilidadeVagaResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DisponibilidadeVagaResponseDTO> createDisponibilidadeVaga(@RequestBody @Valid DisponibilidadeVagaRequestDTO disponibilidadeVagaRequestDTO) {
        Optional<Vaga> vagaOpt = vagaService.findById(disponibilidadeVagaRequestDTO.getVagaId());
        Optional<Usuario> usuarioOpt = usuarioService.findById(disponibilidadeVagaRequestDTO.getCriadoPorId());

        if (vagaOpt.isPresent() && usuarioOpt.isPresent()) {
            DisponibilidadeVaga disponibilidadeVaga = disponibilidadeVagaRequestDTO.toEntity(vagaOpt.get(), usuarioOpt.get());
            DisponibilidadeVaga savedDisponibilidadeVaga = disponibilidadeVagaService.save(disponibilidadeVaga);
            return ResponseEntity.status(HttpStatus.CREATED).body(new DisponibilidadeVagaResponseDTO(savedDisponibilidadeVaga));
        }
        return ResponseEntity.badRequest().build(); // Or a more specific error
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadeVagaResponseDTO> updateDisponibilidadeVaga(@PathVariable UUID id, @RequestBody @Valid DisponibilidadeVagaRequestDTO disponibilidadeVagaRequestDTO) {
        
        Optional<DisponibilidadeVaga> existingOpt = disponibilidadeVagaService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        Optional<Vaga> vagaOpt = vagaService.findById(disponibilidadeVagaRequestDTO.getVagaId());
        Optional<Usuario> usuarioOpt = usuarioService.findById(disponibilidadeVagaRequestDTO.getCriadoPorId());

        if (vagaOpt.isEmpty() || usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().build(); 
        }

        DisponibilidadeVaga existingDisponibilidadeVaga = existingOpt.get();
        existingDisponibilidadeVaga.setVaga(vagaOpt.get());
        existingDisponibilidadeVaga.setInicio(disponibilidadeVagaRequestDTO.getInicio());
        existingDisponibilidadeVaga.setFim(disponibilidadeVagaRequestDTO.getFim());
        existingDisponibilidadeVaga.setCriadoPor(usuarioOpt.get());
        
        DisponibilidadeVaga updatedDisponibilidadeVaga = disponibilidadeVagaService.save(existingDisponibilidadeVaga);
        
        // 200 OK
        return ResponseEntity.ok(new DisponibilidadeVagaResponseDTO(updatedDisponibilidadeVaga));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisponibilidadeVaga(@PathVariable UUID id) {
        if (disponibilidadeVagaService.findById(id).isPresent()) {
            disponibilidadeVagaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
