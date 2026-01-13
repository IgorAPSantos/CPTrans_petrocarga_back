package com.cptrans.petrocarga.utils;

import java.util.List;
import java.util.UUID;

import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;

import com.cptrans.petrocarga.security.UserAuthenticated;


@Component
public class AuthUtils {
    public static void validarPemissoesUsuarioLogado(@AuthenticationPrincipal UserAuthenticated userAuthenticated, UUID usuarioId, List<String> permissoes) {
        if (usuarioId.equals(userAuthenticated.id())) return;
        List<String> authorities = userAuthenticated.userDetails().getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        for(String permissao : permissoes){
            if(authorities.contains(permissao)) return;
        }
        throw new AuthorizationDeniedException("Acesso negado.");
    }
}
