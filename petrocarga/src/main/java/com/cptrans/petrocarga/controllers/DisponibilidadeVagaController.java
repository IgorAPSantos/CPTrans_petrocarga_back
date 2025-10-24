package com.cptrans.petrocarga.controllers;

import com.cptrans.petrocarga.dto.DisponibilidadeVagaRequestDTO;
import com.cptrans.petrocarga.dto.DisponibilidadeVagaResponseDTO;
import com.cptrans.petrocarga.models.DisponibilidadeVaga;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.services.DisponibilidadeVagaService;
import com.cptrans.petrocarga.services.UsuarioService;
import com.cptrans.petrocarga.services.VagaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(disponibilidadeVagas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadeVagaResponseDTO> getDisponibilidadeVagaById(@PathVariable UUID id) {
        return disponibilidadeVagaService.findById(id)
                .map(this::convertToResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DisponibilidadeVagaResponseDTO> createDisponibilidadeVaga(@RequestBody @Valid DisponibilidadeVagaRequestDTO disponibilidadeVagaRequestDTO) {
        Optional<Vaga> vagaOpt = vagaService.findById(disponibilidadeVagaRequestDTO.getVagaId());
        Optional<Usuario> usuarioOpt = usuarioService.findById(disponibilidadeVagaRequestDTO.getCriadoPorId());

        if (vagaOpt.isPresent() && usuarioOpt.isPresent()) {
            DisponibilidadeVaga disponibilidadeVaga = convertToEntity(disponibilidadeVagaRequestDTO, vagaOpt.get(), usuarioOpt.get());
            DisponibilidadeVaga savedDisponibilidadeVaga = disponibilidadeVagaService.save(disponibilidadeVaga);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponseDTO(savedDisponibilidadeVaga));
        }
        return ResponseEntity.badRequest().build(); // Or a more specific error
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadeVagaResponseDTO> updateDisponibilidadeVaga(@PathVariable UUID id, @RequestBody @Valid DisponibilidadeVagaRequestDTO disponibilidadeVagaRequestDTO) {
        return disponibilidadeVagaService.findById(id)
                .map(existingDisponibilidadeVaga -> {
                    Optional<Vaga> vagaOpt = vagaService.findById(disponibilidadeVagaRequestDTO.getVagaId());
                    Optional<Usuario> usuarioOpt = usuarioService.findById(disponibilidadeVagaRequestDTO.getCriadoPorId());

                    if (vagaOpt.isPresent() && usuarioOpt.isPresent()) {
                        updateEntityFromDto(existingDisponibilidadeVaga, disponibilidadeVagaRequestDTO, vagaOpt.get(), usuarioOpt.get());
                        DisponibilidadeVaga updatedDisponibilidadeVaga = disponibilidadeVagaService.save(existingDisponibilidadeVaga);
                        return ResponseEntity.ok(convertToResponseDTO(updatedDisponibilidadeVaga));
                    }
                    return ResponseEntity.badRequest().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisponibilidadeVaga(@PathVariable UUID id) {
        if (disponibilidadeVagaService.findById(id).isPresent()) {
            disponibilidadeVagaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private DisponibilidadeVagaResponseDTO convertToResponseDTO(DisponibilidadeVaga disponibilidadeVaga) {
        DisponibilidadeVagaResponseDTO dto = new DisponibilidadeVagaResponseDTO();
        dto.setId(disponibilidadeVaga.getId());
        dto.setVagaId(disponibilidadeVaga.getVaga().getId());
        dto.setInicio(disponibilidadeVaga.getInicio());
        dto.setFim(disponibilidadeVaga.getFim());
        dto.setCriadoEm(disponibilidadeVaga.getCriadoEm());
        dto.setCriadoPorId(disponibilidadeVaga.getCriadoPor().getId());
        return dto;
    }

    private DisponibilidadeVaga convertToEntity(DisponibilidadeVagaRequestDTO dto, Vaga vaga, Usuario usuario) {
        DisponibilidadeVaga disponibilidadeVaga = new DisponibilidadeVaga();
        disponibilidadeVaga.setVaga(vaga);
        disponibilidadeVaga.setInicio(dto.getInicio());
        disponibilidadeVaga.setFim(dto.getFim());
        disponibilidadeVaga.setCriadoPor(usuario);
        return disponibilidadeVaga;
    }

    private void updateEntityFromDto(DisponibilidadeVaga disponibilidadeVaga, DisponibilidadeVagaRequestDTO dto, Vaga vaga, Usuario usuario) {
        disponibilidadeVaga.setVaga(vaga);
        disponibilidadeVaga.setInicio(dto.getInicio());
        disponibilidadeVaga.setFim(dto.getFim());
        disponibilidadeVaga.setCriadoPor(usuario);
    }
}