package com.cptrans.petrocarga.application.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cptrans.petrocarga.application.service.PushNotificationService;
import com.cptrans.petrocarga.application.service.RealTimeNotificationService;
import com.cptrans.petrocarga.domain.event.NotificacaoCriadaEvent;

@Component
public class NotificacaoCriadaListener {
    
    @Autowired
    private RealTimeNotificationService realTimeNotificationService;
    @Autowired
    private PushNotificationService pushNotificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNotificacaoEnviada(NotificacaoCriadaEvent event) {
        if(realTimeNotificationService.isAtivo(event.notificacao().getUsuarioId())){
            realTimeNotificationService.enviarNotificacao(event.notificacao());
        }else{
            pushNotificationService.enviarNotificacao(event.notificacao());
        }
    }
    
}
