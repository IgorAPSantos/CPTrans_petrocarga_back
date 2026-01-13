package com.cptrans.petrocarga.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:no-reply@petrocarga.test}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendActivationCode(String to, String code) {
        // Log the code for local/dev testing
        LOGGER.info("Ambiente de DEV - Código para {}: {}", to, code);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Código de Ativação - PetroCarga");
        message.setText("Seu código de ativação é: " + code + "\n\nSe você não solicitou, ignore este e-mail.");

        mailSender.send(message);
    }

    public void sendPasswordResetCode(String to, String code) {
        // Log the code for local/dev testing
        LOGGER.info("Ambiente de DEV - Código de recuperação de senha para {}: {}", to, code);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Recuperação de Senha - PetroCarga");
        message.setText("Você solicitou a recuperação de senha.\n\n" +
                "Seu código de recuperação é: " + code + "\n\n" +
                "Este código expira em 10 minutos.\n\n" +
                "Se você não solicitou esta recuperação, ignore este e-mail.");

        mailSender.send(message);
    }
}
