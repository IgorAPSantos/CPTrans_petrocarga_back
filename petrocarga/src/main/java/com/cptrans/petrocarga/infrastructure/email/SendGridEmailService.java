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

/**
 * Serviço de email usando SendGrid HTTP API.
 * 
 * Vantagens sobre Resend free tier:
 * - 100 emails/dia grátis (sem modo sandbox)
 * - Pode enviar para qualquer destinatário (não precisa verificar domínio)
 * - Railway não bloqueia porta 443 (HTTPS)
 * 
 * Configuração necessária no Railway:
 * - SENDGRID_API_KEY: sua API key do SendGrid
 * - SENDGRID_FROM: email remetente verificado (ex: noreply@yourdomain.com)
 */
@Service
@Primary
@ConditionalOnProperty(name = "SENDGRID_API_KEY", matchIfMissing = false)
public class SendGridEmailService implements EmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendGridEmailService.class);
    private static final String SENDGRID_API_URL = "https://api.sendgrid.com/v3/mail/send";

    private final RestTemplate restTemplate;

    @Value("${SENDGRID_API_KEY:}")
    private String apiKey;

    @Value("${SENDGRID_FROM:noreply@petrocarga.test}")
    private String fromEmail;

    @Value("${FRONTEND_URL:http://localhost:3000}")
    private String frontendBaseUrl;

    public SendGridEmailService() {
        this.restTemplate = new RestTemplate();
        LOGGER.info("========================================================");
        LOGGER.info("  SendGridEmailService (HTTP API) initialized");
        LOGGER.info("  100 emails/dia gratis - sem restricao de destinatario");
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
            LOGGER.info("[{}] Sending email via SendGrid API to {}", Thread.currentThread().getName(), to);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // SendGrid API v3 payload format
            String jsonPayload = String.format("""
                {
                  "personalizations": [
                    {
                      "to": [{"email": "%s"}]
                    }
                  ],
                  "from": {"email": "%s"},
                  "subject": "%s",
                  "content": [
                    {
                      "type": "text/plain",
                      "value": "%s"
                    }
                  ]
                }
                """, to, fromEmail, escapeJson(subject), escapeJson(text));

            HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    SENDGRID_API_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("[{}] Email enviado com sucesso via SendGrid API para: {}", 
                    Thread.currentThread().getName(), to);
            } else {
                LOGGER.error("[{}] Falha ao enviar email via SendGrid. Status: {}, Body: {}", 
                    Thread.currentThread().getName(), response.getStatusCode(), response.getBody());
            }

        } catch (Exception e) {
            LOGGER.error("[{}] Erro ao enviar email via SendGrid API para {}: {}", 
                Thread.currentThread().getName(), to, e.getMessage(), e);
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}
