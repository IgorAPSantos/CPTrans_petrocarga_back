package com.cptrans.petrocarga.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.dto.AuthRequestDTO;
import com.cptrans.petrocarga.dto.AuthResponseDTO;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.repositories.UsuarioRepository;
import com.cptrans.petrocarga.security.JwtService;

@Service
public class AuthService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public AuthResponseDTO login(AuthRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas."));
        if(usuario.getAtivo().equals(false)) {
            throw new IllegalArgumentException("Usuário desativado.");
        }
        if(!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new IllegalArgumentException("Credenciais inválidas.");
        }
        String token = jwtService.gerarToken(usuario);

       return new AuthResponseDTO(usuario.toResponseDTO(), token);
    }
}
