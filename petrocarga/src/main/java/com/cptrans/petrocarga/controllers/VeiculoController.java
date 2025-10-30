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

import com.cptrans.petrocarga.dto.VeiculoRequestDTO;
import com.cptrans.petrocarga.dto.VeiculoResponseDTO;
import com.cptrans.petrocarga.models.Veiculo;
import com.cptrans.petrocarga.services.VeiculoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    @GetMapping
    public ResponseEntity<List<VeiculoResponseDTO>> getAllVeiculos() {
        List<VeiculoResponseDTO> veiculos = veiculoService.findAll().stream()
                .map(VeiculoResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(veiculos);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'MOTORISTA', 'EMPRESA')")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<VeiculoResponseDTO>> getVeiculoByUsuarioId(@PathVariable UUID usuarioId) {
        List<VeiculoResponseDTO> veiculos = veiculoService.findByUsuarioId(usuarioId).stream().map(VeiculoResponseDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(veiculos);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MOTORISTA', 'EMPRESA')")
    @PostMapping
    public ResponseEntity<VeiculoResponseDTO> createVeiculo(@RequestBody @Valid VeiculoRequestDTO veiculoRequestDTO) {
        Veiculo novoVeiculo = veiculoService.createVeiculo(veiculoRequestDTO.toEntity(), veiculoRequestDTO.getUsuarioId());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoVeiculo.toResponseDTO());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'MOTORISTA', 'EMPRESA')")
    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> getVeiculoById(@PathVariable UUID id) {
        Veiculo veiculo = veiculoService.findById(id);
        return ResponseEntity.ok(veiculo.toResponseDTO());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'MOTORISTA', 'EMPRESA')")
    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> updateVeiculo(@PathVariable UUID id, @RequestBody @Valid VeiculoRequestDTO veiculoRequestDTO) {
        Veiculo veiculo = veiculoService.updateVeiculo(id, veiculoRequestDTO.toEntity(), veiculoRequestDTO.getUsuarioId());
        return ResponseEntity.ok(veiculo.toResponseDTO());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR','MOTORISTA', 'EMPRESA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVeiculo(@PathVariable UUID id) {
        veiculoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}