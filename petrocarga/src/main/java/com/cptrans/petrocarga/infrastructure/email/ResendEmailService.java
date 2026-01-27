package com.cptrans.petrocarga.infrastructure.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço de email usando a API HTTP do Resend.
 * 
 * O Railway bloqueia TODAS as portas SMTP (25, 465, 587, 2525).
 * A única forma de enviar emails é via API HTTP (porta 443).
 * 
 * Configuração necessária no Railway:
 * - RESEND_API_KEY: sua API key do Resend (re_xxxx)
 * - RESEND_FROM: email remetente (onboarding@resend.dev no tier gratuito)
 */
@Service
@Primary
@ConditionalOnProperty(name = "RESEND_API_KEY", matchIfMissing = false)
public class ResendEmailService implements EmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResendEmailService.class);
    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    private final RestTemplate restTemplate;

    @Value("${RESEND_API_KEY:}")
    private String apiKey;

    @Value("${RESEND_FROM:onboarding@resend.dev}")
    private String fromEmail;

    @Value("${FRONTEND_URL:http://localhost:3000}")
    private String frontendBaseUrl;

    public ResendEmailService() {
        this.restTemplate = new RestTemplate();
        LOGGER.info("========================================================");
        LOGGER.info("  ResendEmailService (HTTP API) initialized");
        LOGGER.info("  Using Resend API instead of SMTP (Railway blocks SMTP)");
        LOGGER.info("========================================================");
    }

    @Override
    @Async("taskExecutor")
    public void sendActivationCode(String to, String code) {
        String subject = "Código de Ativação - PetroCarga";
        String text = "Seu código de ativação é: " + code + "\n\n" +
                "Clique no link abaixo para ativar sua conta:\n" +
                frontendBaseUrl + "/autorizacao/login/\n\n" +
                "Se você não solicitou, ignore este e-mail.";

        sendEmail(to, subject, text);
    }

    @Override
    @Async("taskExecutor")
    public void sendPasswordResetCode(String to, String code) {
        String subject = "Recuperação de Senha - PetroCarga";
        String text = "Você solicitou a recuperação de senha.\n\n" +
                "Seu código de recuperação é: " + code + "\n\n" +
                "Clique no link abaixo para redefinir sua senha:\n" +
                frontendBaseUrl + "/autorizacao/nova-senha/\n\n" +
                "Este código expira em 10 minutos.\n" +
                "Se você não solicitou esta recuperação, ignore este e-mail.";

        sendEmail(to, subject, text);
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            LOGGER.info("[{}] Sending email via Resend API to {}", Thread.currentThread().getName(), to);
            LOGGER.info("From: {}, Subject: {}", fromEmail, subject);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("from", fromEmail);
            body.put("to", List.of(to));
            body.put("subject", subject);
            body.put("text", text);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    RESEND_API_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("[{}] Email enviado com sucesso via Resend API para: {}", 
                        Thread.currentThread().getName(), to);
                LOGGER.debug("Resend response: {}", response.getBody());
            } else {
                LOGGER.error("[{}] Falha ao enviar email via Resend API. Status: {}, Body: {}", 
                        Thread.currentThread().getName(), response.getStatusCode(), response.getBody());
            }

        } catch (Exception e) {
            LOGGER.error("[{}] Erro ao enviar email via Resend API para {}: {}", 
                    Thread.currentThread().getName(), to, e.getMessage(), e);
            throw new RuntimeException("Falha ao enviar email via Resend API", e);
        }
    }
}
