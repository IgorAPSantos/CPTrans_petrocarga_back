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

import com.cptrans.petrocarga.dto.AgenteRequestDTO;
import com.cptrans.petrocarga.dto.AgenteResponseDTO;
import com.cptrans.petrocarga.models.Agente;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.services.AgenteService;
import com.cptrans.petrocarga.services.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/agentes")
public class AgenteController {

    @Autowired
    private AgenteService agenteService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<AgenteResponseDTO>> getAllAgentes() {
        List<AgenteResponseDTO> agentes = agenteService.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(agentes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgenteResponseDTO> getAgenteById(@PathVariable UUID id) {
        return agenteService.findById(id)
                .map(this::convertToResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AgenteResponseDTO> createAgente(@RequestBody @Valid AgenteRequestDTO agenteRequestDTO) {
        return usuarioService.findById(agenteRequestDTO.getUsuarioId())
                .map(usuario -> {
                    Agente agente = convertToEntity(agenteRequestDTO, usuario);
                    Agente savedAgente = agenteService.save(agente);
                    return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponseDTO(savedAgente));
                })
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgenteResponseDTO> updateAgente(@PathVariable UUID id, @RequestBody @Valid AgenteRequestDTO agenteRequestDTO) {
        return agenteService.findById(id)
                .flatMap(existingAgente -> usuarioService.findById(agenteRequestDTO.getUsuarioId())
                        .map(usuario -> {
                            updateEntityFromDto(existingAgente, agenteRequestDTO, usuario);
                            Agente updatedAgente = agenteService.save(existingAgente);
                            return ResponseEntity.ok(convertToResponseDTO(updatedAgente));
                        }))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgente(@PathVariable UUID id) {
        if (agenteService.findById(id).isPresent()) {
            agenteService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private AgenteResponseDTO convertToResponseDTO(Agente agente) {
        AgenteResponseDTO dto = new AgenteResponseDTO();
        dto.setId(agente.getId());
        dto.setMatricula(agente.getMatricula());
        dto.setUsuarioId(agente.getUsuario().getId());
        return dto;
    }

    private Agente convertToEntity(AgenteRequestDTO dto, Usuario usuario) {
        Agente agente = new Agente();
        agente.setMatricula(dto.getMatricula());
        agente.setUsuario(usuario);
        return agente;
    }

    private void updateEntityFromDto(Agente agente, AgenteRequestDTO dto, Usuario usuario) {
        agente.setMatricula(dto.getMatricula());
        agente.setUsuario(usuario);
    }
}