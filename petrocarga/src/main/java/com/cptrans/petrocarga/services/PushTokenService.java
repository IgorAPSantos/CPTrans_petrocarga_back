package com.cptrans.petrocarga.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.models.PushToken;
import com.cptrans.petrocarga.repositories.PushTokenRepository;

@Service
public class PushTokenService {
    @Autowired
    private PushTokenRepository pushTokenRepository;
    
    public PushToken salvar(PushToken novoPushToken){ 
        Optional<PushToken> pushTokenExistente = pushTokenRepository.findByTokenAndUsuarioId(novoPushToken.getToken(), novoPushToken.getUsuarioId());
        
        if (pushTokenExistente.isPresent()) {
            if(!pushTokenExistente.get().getUsuarioId().equals(novoPushToken.getUsuarioId())) throw new IllegalArgumentException("Proibido duplicar token para usuários diferentes.");
            pushTokenExistente.get().setAtivo(true);
            pushTokenExistente.get().setPlataforma(novoPushToken.getPlataforma());
            return pushTokenRepository.save(pushTokenExistente.get());
        } else {
            return pushTokenRepository.save(novoPushToken);
        }

    }
}
