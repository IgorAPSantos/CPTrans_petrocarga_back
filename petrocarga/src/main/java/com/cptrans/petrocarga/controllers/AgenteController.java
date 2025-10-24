package com.cptrans.petrocarga.controllers;

import com.cptrans.petrocarga.dto.ReservaRapidaRequestDTO;
import com.cptrans.petrocarga.dto.ReservaRapidaResponseDTO;
import com.cptrans.petrocarga.models.Agente;
import com.cptrans.petrocarga.models.ReservaRapida;
import com.cptrans.petrocarga.models.Vaga;
import com.cptrans.petrocarga.services.AgenteService;
import com.cptrans.petrocarga.services.ReservaRapidaService;
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
@RequestMapping("/reservas-rapidas")
public class ReservaRapidaController {

    @Autowired
    private ReservaRapidaService reservaRapidaService;

    @Autowired
    private VagaService vagaService;

    @Autowired
    private AgenteService agenteService;

    @GetMapping
    public ResponseEntity<List<ReservaRapidaResponseDTO>> getAllReservaRapidas() {
        List<ReservaRapidaResponseDTO> reservasRapidas = reservaRapidaService.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservasRapidas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaRapidaResponseDTO> getReservaRapidaById(@PathVariable UUID id) {
        return reservaRapidaService.findById(id)
                .map(this::convertToResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ReservaRapidaResponseDTO> createReservaRapida(@RequestBody @Valid ReservaRapidaRequestDTO reservaRapidaRequestDTO) {
        Optional<Vaga> vagaOpt = vagaService.findById(reservaRapidaRequestDTO.getVagaId());
        Optional<Agente> agenteOpt = agenteService.findById(reservaRapidaRequestDTO.getAgenteId());

        if (vagaOpt.isPresent() && agenteOpt.isPresent()) {
            ReservaRapida reservaRapida = convertToEntity(reservaRapidaRequestDTO, vagaOpt.get(), agenteOpt.get());
            ReservaRapida savedReservaRapida = reservaRapidaService.save(reservaRapida);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponseDTO(savedReservaRapida));
        }
        return ResponseEntity.badRequest().build(); // Or a more specific error
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaRapidaResponseDTO> updateReservaRapida(@PathVariable UUID id, @RequestBody @Valid ReservaRapidaRequestDTO reservaRapidaRequestDTO) {
        return reservaRapidaService.findById(id)
                .map(existingReservaRapida -> {
                    Optional<Vaga> vagaOpt = vagaService.findById(reservaRapidaRequestDTO.getVagaId());
                    Optional<Agente> agenteOpt = agenteService.findById(reservaRapidaRequestDTO.getAgenteId());

                    if (vagaOpt.isPresent() && agenteOpt.isPresent()) {
                        updateEntityFromDto(existingReservaRapida, reservaRapidaRequestDTO, vagaOpt.get(), agenteOpt.get());
                        ReservaRapida updatedReservaRapida = reservaRapidaService.save(existingReservaRapida);
                        return ResponseEntity.ok(convertToResponseDTO(updatedReservaRapida));
                    }
                    return ResponseEntity.badRequest().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservaRapida(@PathVariable UUID id) {
        if (reservaRapidaService.findById(id).isPresent()) {
            reservaRapidaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private ReservaRapidaResponseDTO convertToResponseDTO(ReservaRapida reservaRapida) {
        ReservaRapidaResponseDTO dto = new ReservaRapidaResponseDTO();
        dto.setId(reservaRapida.getId());
        dto.setVagaId(reservaRapida.getVaga().getId());
        dto.setAgenteId(reservaRapida.getAgente().getId());
        dto.setTipoVeiculo(reservaRapida.getTipoVeiculo());
        dto.setPlaca(reservaRapida.getPlaca());
        dto.setInicio(reservaRapida.getInicio());
        dto.setFim(reservaRapida.getFim());
        dto.setCriadoEm(reservaRapida.getCriadoEm());
        return dto;
    }

    private ReservaRapida convertToEntity(ReservaRapidaRequestDTO dto, Vaga vaga, Agente agente) {
        ReservaRapida reservaRapida = new ReservaRapida();
        reservaRapida.setVaga(vaga);
        reservaRapida.setAgente(agente);
        reservaRapida.setTipoVeiculo(dto.getTipoVeiculo());
        reservaRapida.setPlaca(dto.getPlaca());
        reservaRapida.setInicio(dto.getInicio());
        reservaRapida.setFim(dto.getFim());
        return reservaRapida;
    }

    private void updateEntityFromDto(ReservaRapida reservaRapida, ReservaRapidaRequestDTO dto, Vaga vaga, Agente agente) {
        reservaRapida.setVaga(vaga);
        reservaRapida.setAgente(agente);
        reservaRapida.setTipoVeiculo(dto.getTipoVeiculo());
        reservaRapida.setPlaca(dto.getPlaca());
        reservaRapida.setInicio(dto.getInicio());
        reservaRapida.setFim(dto.getFim());
    }
}