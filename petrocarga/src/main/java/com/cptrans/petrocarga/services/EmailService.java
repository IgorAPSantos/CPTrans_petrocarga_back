package com.cptrans.petrocarga.services;

/*
 * EmailService
 *
 * Notes for deployment (Railway):
 * - Required environment variables (set in Railway project settings):
 *   SMTP_HOST (e.g. smtp.gmail.com)
 *   SMTP_PORT (e.g. 465 for SSL)
 *   SMTP_USERNAME
 *   SMTP_PASSWORD
 *   SMTP_FROM (optional, defaults to SMTP_USERNAME)
 *
 * How to validate email delivery in logs:
 * - Look for lines like "Mail sender config - host: ..., port: ..., from: ..." before send.
 * - Successful send will log: "Email ... enviado com sucesso para: <email>" with thread name.
 * - Failures will be logged with MailException and stacktrace.
 *
 * How to test the endpoint (/auth/forgot-password) when send is asynchronous:
 * 1) Call POST /auth/forgot-password with the email body.
 * 2) Controller returns 200 OK immediately.
 * 3) Check application logs for the background task entries described above.
 * 4) For end-to-end validation, verify the recipient inbox (or use SMTP testing service).
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:${spring.mail.username:no-reply@petrocarga.test}}")
    private String from;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private void logMailEndpointInfo() {
        try {
            if (mailSender instanceof JavaMailSenderImpl) {
                JavaMailSenderImpl impl = (JavaMailSenderImpl) mailSender;
                String host = impl.getHost();
                int port = impl.getPort();
                // Do NOT log password or sensitive props
                LOGGER.info("Mail sender config - host: {}, port: {}, from: {}", host, port, from);
            } else {
                LOGGER.info("Mail sender is not JavaMailSenderImpl, cannot read host/port. from={}", from);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to inspect JavaMailSender implementation: {}", e.getMessage());
        }
    }

    @Async("taskExecutor")
    public void sendActivationCode(String to, String code) {
        // Ensure 'from' uses configured username when available
        if ((from == null || from.isBlank()) && mailUsername != null && !mailUsername.isBlank()) {
            from = mailUsername;
        }

        logMailEndpointInfo();

        try {
            LOGGER.info("[{}] Sending activation code to {}", Thread.currentThread().getName(), to);
            LOGGER.debug("Activation code for {}: {}", to, code);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("Código de Ativação - PetroCarga");
            message.setText(
                "Seu código de ativação é: " + code + "\n\n" +
                "Clique no link abaixo para ativar sua conta:\n" +
                frontendBaseUrl + "/autorizacao/login/\n\n" +
                "Se você não solicitou, ignore este e-mail.");

            mailSender.send(message);
            LOGGER.info("[{}] Email de ativação enviado com sucesso para: {}", Thread.currentThread().getName(), to);
        } catch (MailException e) {
            LOGGER.error("[{}] MailException ao enviar ativação para {}: {}", Thread.currentThread().getName(), to, e.getMessage(), e);
            // Não relançar: método assíncrono deve tratar falhas internamente
        } catch (Exception e) {
            LOGGER.error("[{}] Erro inesperado ao enviar ativação para {}: {}", Thread.currentThread().getName(), to, e.getMessage(), e);
        }
    }

    @Async("taskExecutor")
    public void sendPasswordResetCode(String to, String code) {
        if ((from == null || from.isBlank()) && mailUsername != null && !mailUsername.isBlank()) {
            from = mailUsername;
        }

        logMailEndpointInfo();

        try {
            LOGGER.info("[{}] Sending password reset to {}", Thread.currentThread().getName(), to);
            LOGGER.debug("Password reset code for {}: {}", to, code);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("Recuperação de Senha - PetroCarga");
            message.setText("Você solicitou a recuperação de senha.\n\n" +
                    "Seu código de recuperação é: " + code + "\n\n" +
                    "Clique no link abaixo para redefinir sua senha:\n" +
                    frontendBaseUrl + "/autorizacao/nova-senha/\n\n" +
                    "Este código expira em 10 minutos.\n" +
                    "Se você não solicitou esta recuperação, ignore este e-mail.");

            mailSender.send(message);
            LOGGER.info("[{}] Email de reset de senha enviado com sucesso para: {}", Thread.currentThread().getName(), to);
        } catch (MailException e) {
            LOGGER.error("[{}] MailException ao enviar reset para {}: {}", Thread.currentThread().getName(), to, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("[{}] Erro inesperado ao enviar reset para {}: {}", Thread.currentThread().getName(), to, e.getMessage(), e);
        }
    }
}
