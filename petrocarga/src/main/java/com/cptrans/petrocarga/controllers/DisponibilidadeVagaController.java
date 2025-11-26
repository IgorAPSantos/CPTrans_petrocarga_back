package com.cptrans.petrocarga.controllers;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.cptrans.petrocarga.dto.MultiplasDisponibilidadesVagaRequestDTO;
import com.cptrans.petrocarga.models.DisponibilidadeVaga;
import com.cptrans.petrocarga.services.DisponibilidadeVagaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/disponibilidade-vagas")
public class DisponibilidadeVagaController {

    @Autowired
    private DisponibilidadeVagaService disponibilidadeVagaService;

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'AGENTE', 'MOTORISTA', 'EMPRESA')")
    @GetMapping
    public ResponseEntity<List<DisponibilidadeVagaResponseDTO>> getAllDisponibilidadeVagas() {
        List<DisponibilidadeVagaResponseDTO> disponibilidadeVagas = disponibilidadeVagaService.findAll().stream()
                .map(DisponibilidadeVagaResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(disponibilidadeVagas);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'AGENTE', 'MOTORISTA', 'EMPRESA')")
    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadeVagaResponseDTO> getDisponibilidadeVagaById(@PathVariable UUID id) {
        DisponibilidadeVaga disponibilidadeVaga = disponibilidadeVagaService.findById(id);
        return ResponseEntity.ok(disponibilidadeVaga.toResponseDTO());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'AGENTE', 'MOTORISTA', 'EMPRESA')")
    @GetMapping("/vaga/{vagaId}")
    public ResponseEntity<List<DisponibilidadeVagaResponseDTO>> getDisponibilidadeVagaByVagaId(@PathVariable UUID vagaId) {
        List<DisponibilidadeVagaResponseDTO> disponibilidadeVaga = disponibilidadeVagaService.findByVagaId(vagaId).stream().map(DisponibilidadeVagaResponseDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(disponibilidadeVaga);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    @PostMapping
    public ResponseEntity<DisponibilidadeVagaResponseDTO> createDisponibilidadeVaga(@RequestBody @Valid DisponibilidadeVagaRequestDTO disponibilidadeVagaRequestDTO) {
        DisponibilidadeVaga savedDisponibilidadeVaga = disponibilidadeVagaService.createDisponibilidadeVaga(disponibilidadeVagaRequestDTO.toEntity(), disponibilidadeVagaRequestDTO.getVagaId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDisponibilidadeVaga.toResponseDTO());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    @PostMapping("/vagas")
    public ResponseEntity<List<DisponibilidadeVagaResponseDTO>> createMultipleDisponibilidadeVagas(@RequestBody @Valid MultiplasDisponibilidadesVagaRequestDTO multiplasDisponibilidadesVagaRequestDTO) {
        List<DisponibilidadeVaga> savedDisponibilidadeVagas = disponibilidadeVagaService.createMultipleDisponibilidadeVagas(multiplasDisponibilidadesVagaRequestDTO.toEntity(), multiplasDisponibilidadesVagaRequestDTO.getListaVagaId());
        List<DisponibilidadeVagaResponseDTO> response = savedDisponibilidadeVagas.stream()
                .map(DisponibilidadeVaga::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadeVagaResponseDTO> updateDisponibilidadeVaga(@PathVariable UUID id, @RequestBody @Valid DisponibilidadeVagaRequestDTO disponibilidadeVagaRequestDTO) {
        DisponibilidadeVaga disponibilidadeVaga = disponibilidadeVagaService.updateDisponibilidadeVaga(id, disponibilidadeVagaRequestDTO.toEntity(), disponibilidadeVagaRequestDTO.getVagaId());
        return ResponseEntity.ok(disponibilidadeVaga.toResponseDTO());
      
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisponibilidadeVaga(@PathVariable UUID id) {
        disponibilidadeVagaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
