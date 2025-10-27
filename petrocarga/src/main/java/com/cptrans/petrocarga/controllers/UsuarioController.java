package com.cptrans.petrocarga.controllers;

import com.cptrans.petrocarga.dto.UsuarioRequestDTO;
import com.cptrans.petrocarga.dto.UsuarioResponseDTO;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> getAllUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.findAll().stream()
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioById(@PathVariable UUID id) {
        return usuarioService.findById(id)
                .map(UsuarioResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> createUsuario(@RequestBody @Valid UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuario = usuarioRequestDTO.toEntity();
        Usuario savedUsuario = usuarioService.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioResponseDTO(savedUsuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> updateUsuario(@PathVariable UUID id, @RequestBody @Valid UsuarioRequestDTO usuarioRequestDTO) {
        return usuarioService.findById(id)
                .map(existingUsuario -> {
                    existingUsuario.setNome(usuarioRequestDTO.getNome());
                    existingUsuario.setCpf(usuarioRequestDTO.getCpf());
                    existingUsuario.setTelefone(usuarioRequestDTO.getTelefone());
                    existingUsuario.setEmail(usuarioRequestDTO.getEmail());
                    existingUsuario.setSenha(usuarioRequestDTO.getSenha());
                    existingUsuario.setPermissao(usuarioRequestDTO.getPermissao());
                    Usuario updatedUsuario = usuarioService.save(existingUsuario);
                    return ResponseEntity.ok(new UsuarioResponseDTO(updatedUsuario));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable UUID id) {
        if (usuarioService.findById(id).isPresent()) {
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
