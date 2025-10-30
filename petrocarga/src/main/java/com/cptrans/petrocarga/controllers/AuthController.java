package com.cptrans.petrocarga.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptrans.petrocarga.dto.AuthRequestDTO;
import com.cptrans.petrocarga.dto.AuthResponseDTO;
import com.cptrans.petrocarga.dto.UsuarioRequestDTO;
import com.cptrans.petrocarga.dto.UsuarioResponseDTO;
import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.services.AuthService;
import com.cptrans.petrocarga.services.UsuarioService;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // TODO: Remover instância depois de cadastrar o primeiro admin em deploy
    @Autowired
    private UsuarioService usuarioService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    //TODO: Remover rota depois de cadastrar o primeiro admin em deploy
    @PostMapping("/admin")
    public ResponseEntity<UsuarioResponseDTO> createAdmin(@RequestBody UsuarioRequestDTO usuario) {
        Usuario novoUsuario = usuarioService.createUsuario(usuario.toEntity(), PermissaoEnum.ADMIN);
        return ResponseEntity.ok(novoUsuario.toResponseDTO());
    }

}