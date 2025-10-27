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
                .map(reservaRapida -> new ReservaRapidaResponseDTO(reservaRapida))
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservasRapidas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaRapidaResponseDTO> getReservaRapidaById(@PathVariable UUID id) {
        return reservaRapidaService.findById(id)
                .map(reservaRapida -> new ReservaRapidaResponseDTO(reservaRapida))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ReservaRapidaResponseDTO> createReservaRapida(@RequestBody @Valid ReservaRapidaRequestDTO reservaRapidaRequestDTO) {
        Optional<Vaga> vagaOpt = vagaService.findById(reservaRapidaRequestDTO.getVagaId());
        Optional<Agente> agenteOpt = agenteService.findById(reservaRapidaRequestDTO.getAgenteId());

        if (vagaOpt.isPresent() && agenteOpt.isPresent()) {
            ReservaRapida reservaRapida = reservaRapidaRequestDTO.toEntity(vagaOpt.get(), agenteOpt.get());
            ReservaRapida savedReservaRapida = reservaRapidaService.save(reservaRapida);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ReservaRapidaResponseDTO(savedReservaRapida));
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaRapidaResponseDTO> updateReservaRapida(@PathVariable UUID id, @RequestBody @Valid ReservaRapidaRequestDTO reservaRapidaRequestDTO) {
        
        // 1. Verificar se a entidade a ser atualizada (ReservaRapida) existe
        Optional<ReservaRapida> existingOpt = reservaRapidaService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        // 2. Verificar as dependências
        Optional<Vaga> vagaOpt = vagaService.findById(reservaRapidaRequestDTO.getVagaId());
        Optional<Agente> agenteOpt = agenteService.findById(reservaRapidaRequestDTO.getAgenteId());

        // 3. Validar se as dependências foram encontradas
        if (vagaOpt.isEmpty() || agenteOpt.isEmpty()) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request (dependências ausentes)
        }
        
        // 4. Todas as verificações passaram, realizar a atualização
        ReservaRapida existingReservaRapida = existingOpt.get();
        existingReservaRapida.setVaga(vagaOpt.get());
        existingReservaRapida.setAgente(agenteOpt.get());
        existingReservaRapida.setTipoVeiculo(reservaRapidaRequestDTO.getTipoVeiculo());
        existingReservaRapida.setPlaca(reservaRapidaRequestDTO.getPlaca());
        existingReservaRapida.setInicio(reservaRapidaRequestDTO.getInicio());
        existingReservaRapida.setFim(reservaRapidaRequestDTO.getFim());
        
        ReservaRapida updatedReservaRapida = reservaRapidaService.save(existingReservaRapida);
        
        // 200 OK
        return ResponseEntity.ok(new ReservaRapidaResponseDTO(updatedReservaRapida));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservaRapida(@PathVariable UUID id) {
        if (reservaRapidaService.findById(id).isPresent()) {
            reservaRapidaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
