package com.cptrans.petrocarga.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.models.Usuario;

@Service
public class GestorService {
    @Autowired
    private UsuarioService usuarioService;

    public Usuario createGestor(Usuario novoGestor) {
        return usuarioService.createUsuario(novoGestor, PermissaoEnum.GESTOR);
    }

    public Usuario updateGestor(UUID id, Usuario novoGestor) {
        return usuarioService.updateUsuario(id, novoGestor, PermissaoEnum.GESTOR);
    }
}
