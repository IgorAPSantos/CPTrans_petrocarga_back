package com.cptrans.petrocarga.utils;

import org.springframework.stereotype.Component;

import com.cptrans.petrocarga.enums.PermissaoEnum;

@Component
public class NotificacaoUtils {
    
    public static void validateByPermissao(PermissaoEnum permissaoUsuarioLogado,  PermissaoEnum permissaoDestinatario) {
        switch (permissaoUsuarioLogado) {
            case ADMIN, GESTOR, AGENTE -> {
                break;
            }
            case EMPRESA -> throw new IllegalArgumentException("Usuários com permissão EMPRESA não podem enviar notificações para outros usuários.");
            case MOTORISTA -> throw new IllegalArgumentException("Usuários com permissão MOTORISTA não podem enviar notificações para outros usuários.");
            default -> throw new IllegalArgumentException("Permissão de usuário inválida.");
        }
        
    }
}

