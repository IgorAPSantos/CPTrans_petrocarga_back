package com.cptrans.petrocarga.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cptrans.petrocarga.dto.GestorRequestDTO;
import com.cptrans.petrocarga.dto.UsuarioPATCHRequestDTO;
import com.cptrans.petrocarga.dto.UsuarioResponseDTO;
import com.cptrans.petrocarga.services.GestorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/gestores")
public class GestorController {
    @Autowired
    private GestorService gestorService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<UsuarioResponseDTO>> getAllGestores(@RequestParam(required = false) Boolean ativo) {
        if(ativo != null) {
            return ResponseEntity.ok(gestorService.findAllByAtivo(ativo).stream()
                    .map(gestor -> gestor.toResponseDTO())
                    .toList());
        }

        List<UsuarioResponseDTO> gestores = gestorService.findAll().stream()
                .map(gestor -> gestor.toResponseDTO())
                .toList();
        return ResponseEntity.ok(gestores);
    }

    @PreAuthorize("#usuarioId == authentication.principal.id or hasRole('ADMIN')")
    @GetMapping("/{usuarioId}")
    public ResponseEntity<UsuarioResponseDTO> getGestorById(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(gestorService.findByUsuarioId(usuarioId).toResponseDTO());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<UsuarioResponseDTO> createGestor(@RequestBody @Valid GestorRequestDTO gestorRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gestorService.createGestor(gestorRequestDTO.toEntity()).toResponseDTO());
    }

    @PreAuthorize("#usuarioId == authentication.principal.id or hasRole('ADMIN')")
    @PatchMapping("/{usuarioId}")
    public ResponseEntity<UsuarioResponseDTO> updateGestor(@PathVariable UUID usuarioId, @RequestBody @Valid UsuarioPATCHRequestDTO gestorRequestDTO) {
        return ResponseEntity.ok(gestorService.updateGestor(usuarioId, gestorRequestDTO).toResponseDTO());
    }
}
