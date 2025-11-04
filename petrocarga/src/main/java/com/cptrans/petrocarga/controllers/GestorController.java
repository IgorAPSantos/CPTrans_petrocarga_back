package com.cptrans.petrocarga.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptrans.petrocarga.dto.GestorRequestDTO;
import com.cptrans.petrocarga.dto.UsuarioResponseDTO;
import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.services.UsuarioService;

@RestController
@RequestMapping("/gestor")
public class GestorController {
    @Autowired
    private UsuarioService usuarioService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<UsuarioResponseDTO> createGestor(@RequestBody GestorRequestDTO gestorRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.createUsuario(gestorRequestDTO.toEntity(), PermissaoEnum.GESTOR).toResponseDTO());
    }
}
