package com.cptrans.petrocarga.application.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cptrans.petrocarga.domain.event.UsuarioCriadoEvent;
import com.cptrans.petrocarga.infrastructure.email.EmailSender;

@Component
public class UsuarioCriadoListener {
    @Autowired
    private EmailSender emailSender;

    @TransactionalEventListener( phase = TransactionPhase.AFTER_COMMIT )
    public void onUsuarioCriado(UsuarioCriadoEvent event) {
        emailSender.sendActivationCode(event.email(), event.codigo());
    }
}
