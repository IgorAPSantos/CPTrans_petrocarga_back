package com.cptrans.petrocarga.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.repositories.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(UUID id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado."));
    }

    public List<Usuario> findByPermissao(PermissaoEnum permissao) {
        return usuarioRepository.findByPermissao(permissao);
    }

    public List<Usuario> findByPermissaoAndAtivo(PermissaoEnum permissao, Boolean ativo) {
        return usuarioRepository.findByPermissaoAndAtivo(permissao, ativo);
    }

    public Usuario createUsuario(Usuario novoUsuario, PermissaoEnum permissao) {
        if(usuarioRepository.findByEmail(novoUsuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        if(usuarioRepository.findByCpf(novoUsuario.getCpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        novoUsuario.setPermissao(permissao);
        novoUsuario.setSenha(passwordEncoder.encode(novoUsuario.getSenha()));
        return usuarioRepository.save(novoUsuario);
    }

    public Usuario updateUsuario(UUID id, Usuario novoUsuario, PermissaoEnum permissao) {
        Usuario usuarioExistente = findById(id);

        if(!usuarioExistente.getEmail().equals(novoUsuario.getEmail())) {
            Optional<Usuario> usuarioByEmail = usuarioRepository.findByEmail(novoUsuario.getEmail());
            if (usuarioByEmail.isPresent() && !usuarioByEmail.get().getId().equals(id)) {
                throw new IllegalArgumentException("Email já cadastrado");
            }
            usuarioExistente.setEmail(novoUsuario.getEmail());
        }

        if (!usuarioExistente.getCpf().equals(novoUsuario.getCpf())) {
            Optional<Usuario> usuarioByCpf = usuarioRepository.findByCpf(novoUsuario.getCpf());
            if (usuarioByCpf.isPresent() && !usuarioByCpf.get().getId().equals(id)) {
                throw new IllegalArgumentException("CPF já cadastrado");
            }
            usuarioExistente.setCpf(novoUsuario.getCpf());
        }

        usuarioExistente.setNome(novoUsuario.getNome());
        usuarioExistente.setTelefone(novoUsuario.getTelefone());

        if (novoUsuario.getSenha() != null && !novoUsuario.getSenha().isEmpty()) {
            usuarioExistente.setSenha(passwordEncoder.encode(novoUsuario.getSenha()));
        }
        usuarioExistente.setPermissao(permissao);
        return usuarioRepository.save(usuarioExistente);
    }

    public void deleteById(UUID id) {
        usuarioRepository.deleteById(id);
    }
}
