package com.cptrans.petrocarga.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptrans.petrocarga.dto.GestorPUTRequestDTO;
import com.cptrans.petrocarga.dto.GestorRequestDTO;
import com.cptrans.petrocarga.dto.UsuarioResponseDTO;
import com.cptrans.petrocarga.services.GestorService;

@RestController
@RequestMapping("/gestor")
public class GestorController {
    @Autowired
    private GestorService gestorService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<UsuarioResponseDTO> createGestor(@RequestBody GestorRequestDTO gestorRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gestorService.createGestor(gestorRequestDTO.toEntity()).toResponseDTO());
    }

    @PreAuthorize("#usuarioId == authentication.principal.id or hasRole('ADMIN')")
    @PutMapping("/{usuarioId}")
    public ResponseEntity<UsuarioResponseDTO> updateGestor(@PathVariable UUID usuarioId, @RequestBody GestorPUTRequestDTO gestorRequestDTO) {
        return ResponseEntity.ok(gestorService.updateGestor(usuarioId, gestorRequestDTO.toEntity()).toResponseDTO());
    }
}
