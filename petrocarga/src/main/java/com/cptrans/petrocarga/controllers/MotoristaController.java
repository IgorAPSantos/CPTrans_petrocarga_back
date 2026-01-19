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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cptrans.petrocarga.dto.MotoristaFiltrosDTO;
import com.cptrans.petrocarga.dto.MotoristaRequestDTO;
import com.cptrans.petrocarga.dto.MotoristaResponseDTO;
import com.cptrans.petrocarga.dto.UsuarioPATCHRequestDTO;
import com.cptrans.petrocarga.models.Motorista;
import com.cptrans.petrocarga.services.MotoristaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/motoristas")
public class MotoristaController {

    @Autowired
    private MotoristaService motoristaService;

    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    @GetMapping
    public ResponseEntity<List<MotoristaResponseDTO>> getAllMotoristas(
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) String cpf,
        @RequestParam(required = false) String cnh,
        @RequestParam(required = false) Boolean ativo
    ) {
        MotoristaFiltrosDTO filtros = new MotoristaFiltrosDTO(nome, cpf, cnh, ativo);
        if(filtros.nome() != null || filtros.cpf() != null || filtros.cnh() != null || filtros.ativo() != null) {
            List<MotoristaResponseDTO> motoristasFiltrados = motoristaService.findAllWithFiltros(filtros).stream()
                    .map(MotoristaResponseDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(motoristasFiltrados);
        }
        List<MotoristaResponseDTO> motoristas = motoristaService.findAll().stream()
                .map(MotoristaResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(motoristas);
    }

    @PreAuthorize("#usuarioId == authentication.principal.id or hasRole('ADMIN')")
    @GetMapping("/{usuarioId}")
    public ResponseEntity<MotoristaResponseDTO> getMotoristaById(@PathVariable UUID usuarioId) {
        Motorista motorista = motoristaService.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(motorista.toResponseDTO());
    }

    @PostMapping("/cadastro")
    public ResponseEntity<MotoristaResponseDTO> createMotorista(@RequestBody @Valid MotoristaRequestDTO motoristaRequestDTO) {
        Motorista savedMotorista = motoristaService.createMotorista(motoristaRequestDTO.toEntity(null));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMotorista.toResponseDTO());
    }

    @PreAuthorize("#usuarioId == authentication.principal.id or hasRole('ADMIN')")
    @PatchMapping("/{usuarioId}")
    public ResponseEntity<MotoristaResponseDTO> updateMotorista(@PathVariable UUID usuarioId,  @RequestBody @Valid UsuarioPATCHRequestDTO motoristaRequestDTO) {
        Motorista updatedMotorista = motoristaService.updateMotorista(usuarioId, motoristaRequestDTO);
        return ResponseEntity.ok(new MotoristaResponseDTO(updatedMotorista));
    }

    @PreAuthorize("#usuarioId == authentication.principal.id or hasRole('ADMIN')")
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> deleteMotorista(@PathVariable UUID usuarioId) {
        motoristaService.deleteByUsuarioId(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
