package com.cptrans.petrocarga.controllers;

import java.util.List;
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

import com.cptrans.petrocarga.dto.VeiculoRequestDTO;
import com.cptrans.petrocarga.dto.VeiculoResponseDTO;
import com.cptrans.petrocarga.models.Veiculo;
import com.cptrans.petrocarga.services.UsuarioService;
import com.cptrans.petrocarga.services.VeiculoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<VeiculoResponseDTO>> getAllVeiculos() {
        List<VeiculoResponseDTO> veiculos = veiculoService.findAll().stream()
                .map(VeiculoResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(veiculos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> getVeiculoById(@PathVariable UUID id) {
        return veiculoService.findById(id)
                .map(VeiculoResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VeiculoResponseDTO> createVeiculo(@RequestBody @Valid VeiculoRequestDTO veiculoRequestDTO) {
        return usuarioService.findById(veiculoRequestDTO.getUsuarioId())
                .map(usuario -> {
                    Veiculo veiculo = veiculoRequestDTO.toEntity(usuario);
                    Veiculo savedVeiculo = veiculoService.save(veiculo);
                    return ResponseEntity.status(HttpStatus.CREATED).body(new VeiculoResponseDTO(savedVeiculo));
                })
                .orElse(ResponseEntity.badRequest().build()); // Or a more specific error
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponseDTO> updateVeiculo(@PathVariable UUID id, @RequestBody @Valid VeiculoRequestDTO veiculoRequestDTO) {
        return veiculoService.findById(id)
                .map(existingVeiculo -> {
                    return usuarioService.findById(veiculoRequestDTO.getUsuarioId())
                            .map(usuario -> {
                                existingVeiculo.setPlaca(veiculoRequestDTO.getPlaca());
                                existingVeiculo.setMarca(veiculoRequestDTO.getMarca());
                                existingVeiculo.setModelo(veiculoRequestDTO.getModelo());
                                existingVeiculo.setTipo(veiculoRequestDTO.getTipo());
                                existingVeiculo.setComprimento(veiculoRequestDTO.getComprimento());
                                existingVeiculo.setUsuario(usuario);
                                existingVeiculo.setCpfProprietario(veiculoRequestDTO.getCpfProprietario());
                                existingVeiculo.setCnpjProprietario(veiculoRequestDTO.getCnpjProprietario());
                                Veiculo updatedVeiculo = veiculoService.save(existingVeiculo);
                                return ResponseEntity.ok(new VeiculoResponseDTO(updatedVeiculo));
                            })
                            .orElse(ResponseEntity.badRequest().build());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVeiculo(@PathVariable UUID id) {
        if (veiculoService.findById(id).isPresent()) {
            veiculoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}