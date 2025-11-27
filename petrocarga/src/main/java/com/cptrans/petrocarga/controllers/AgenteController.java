package com.cptrans.petrocarga.controllers;

import java.util.List;
import java.util.UUID;

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

import com.cptrans.petrocarga.dto.AgentePUTRequestDTO;
import com.cptrans.petrocarga.dto.AgenteRequestDTO;
import com.cptrans.petrocarga.dto.AgenteResponseDTO;
import com.cptrans.petrocarga.models.Agente;
import com.cptrans.petrocarga.services.AgenteService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/agentes")
public class AgenteController {

    @Autowired
    private AgenteService agenteService;

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    @GetMapping
    public ResponseEntity<List<AgenteResponseDTO>> getAllAgentes() {
        List<Agente> agentes = agenteService.findAll();
        List<AgenteResponseDTO> response = agentes.stream().map(Agente::toResponseDTO).toList();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize(" #usuarioId == authentication.principal.id or hasAnyRole('ADMIN', 'GESTOR')")
    @GetMapping("/{usuarioId}")
    public ResponseEntity<AgenteResponseDTO> getAgenteById(@PathVariable UUID usuarioId) {
        Agente agente = agenteService.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(agente.toResponseDTO());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    @PostMapping
    public ResponseEntity<AgenteResponseDTO> createAgente(@RequestBody @Valid AgenteRequestDTO agenteRequestDTO) {
        Agente savedAgente = agenteService.createAgente(agenteRequestDTO.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAgente.toResponseDTO());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR') or #usuarioId == authentication.principal.id")
    @PutMapping("/{usuarioId}")
    public ResponseEntity<AgenteResponseDTO> updateAgente(@PathVariable UUID usuarioId, @RequestBody @Valid AgentePUTRequestDTO agenteRequestDTO) {
        Agente updatedAgente = agenteService.updateAgente(usuarioId, agenteRequestDTO.toEntity());
        return ResponseEntity.ok(updatedAgente.toResponseDTO());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> deleteAgente(@PathVariable UUID usuarioId) {
        agenteService.deleteByUsuarioId(usuarioId);
        return ResponseEntity.noContent().build();
    }
}