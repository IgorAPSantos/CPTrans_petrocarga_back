package com.cptrans.petrocarga.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.dto.UsuarioPATCHRequestDTO;
import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.models.Usuario;

@Service
public class GestorService {
    @Autowired
    private UsuarioService usuarioService;

    public List<Usuario> findAll() {
        return usuarioService.findByPermissao(PermissaoEnum.GESTOR);
    }

    public List<Usuario> findAllByAtivo(Boolean ativo) {
        return usuarioService.findByPermissaoAndAtivo(PermissaoEnum.GESTOR, ativo);
    }

    public Usuario findByUsuarioId(UUID usuarioId) {
        return usuarioService.findById(usuarioId);
    }

    public Usuario createGestor(Usuario novoGestor) {
        return usuarioService.createUsuario(novoGestor, PermissaoEnum.GESTOR);
    }

    public Usuario updateGestor(UUID id, UsuarioPATCHRequestDTO novoGestor) {
        return usuarioService.patchUpdate(id, PermissaoEnum.GESTOR, novoGestor);
    }
}
