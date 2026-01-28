package com.cptrans.petrocarga.infrastructure.email;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Properties;

/**
 * =============================================================================
 * GmailApiEmailService - POC para envio de emails via Gmail API
 * =============================================================================
 * 
 * CONTEXTO:
 * - Railway bloqueia todas as portas SMTP (25, 465, 587, 2525)
 * - SendGrid/Resend com @gmail.com como remetente são bloqueados por DMARC
 * - A solução é usar a Gmail API diretamente, que envia emails autênticos
 * 
 * VANTAGENS:
 * - Emails saem diretamente do Gmail (100% autênticos)
 * - Não são bloqueados por DMARC
 * - Não precisa de domínio próprio
 * - Gratuito (100 emails/dia conta pessoal)
 * 
 * LIMITAÇÕES (POC):
 * - Limite de 100 emails/dia (conta pessoal) ou 2000/dia (Workspace)
 * - Requer configuração manual no Google Cloud Console
 * - Refresh token expira se não usado por 6 meses
 * - NÃO RECOMENDADO para produção em escala
 * 
 * CONFIGURAÇÃO NECESSÁRIA (variáveis de ambiente):
 * - GMAIL_CLIENT_ID: Client ID do OAuth 2.0
 * - GMAIL_CLIENT_SECRET: Client Secret do OAuth 2.0
 * - GMAIL_REFRESH_TOKEN: Refresh token obtido na autorização
 * - GMAIL_FROM: Email remetente (ex: cpt.petrocarga@gmail.com)
 * 
 * =============================================================================
 */
@Service
@Primary
@ConditionalOnProperty(name = "GMAIL_REFRESH_TOKEN", matchIfMissing = false)
public class GmailApiEmailService implements EmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(GmailApiEmailService.class);
    private static final String APPLICATION_NAME = "PetroCarga Email Service";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${GMAIL_CLIENT_ID:}")
    private String clientId;

    @Value("${GMAIL_CLIENT_SECRET:}")
    private String clientSecret;

    @Value("${GMAIL_REFRESH_TOKEN:}")
    private String refreshToken;

    @Value("${GMAIL_FROM:}")
    private String fromEmail;

    @Value("${FRONTEND_URL:http://localhost:3000}")
    private String frontendBaseUrl;

    private Gmail gmailService;

    /**
     * Inicializa o serviço Gmail API após a injeção das propriedades.
     * Usa refresh token para obter access tokens automaticamente.
     */
    @PostConstruct
    public void init() {
        try {
            LOGGER.info("========================================================");
            LOGGER.info("  GmailApiEmailService (Gmail API) initializing...");
            LOGGER.info("  From: {}", fromEmail);
            
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            
            // Cria credential usando refresh token
            // Nota: GoogleCredential está deprecated mas funciona para POC
            // Em produção, usar GoogleAuthorizationCodeFlow
            Credential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(JSON_FACTORY)
                    .setClientSecrets(clientId, clientSecret)
                    .build()
                    .setRefreshToken(refreshToken);
            
            // Força refresh do access token para validar configuração
            if (!credential.refreshToken()) {
                LOGGER.error("  FALHA: Não foi possível obter access token!");
                LOGGER.error("  Verifique GMAIL_CLIENT_ID, GMAIL_CLIENT_SECRET e GMAIL_REFRESH_TOKEN");
                LOGGER.info("========================================================");
                return;
            }
            
            // Cria serviço Gmail
            gmailService = new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            
            LOGGER.info("  Gmail API inicializada com SUCESSO!");
            LOGGER.info("  Emails serão enviados autenticamente via Gmail");
            LOGGER.info("========================================================");
            
        } catch (GeneralSecurityException | IOException e) {
            LOGGER.error("========================================================");
            LOGGER.error("  FALHA ao inicializar Gmail API: {}", e.getMessage());
            LOGGER.error("========================================================", e);
        }
    }

    /**
     * Envia código de ativação de conta.
     */
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

    /**
     * Envia código de recuperação de senha.
     */
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

    /**
     * Envia email via Gmail API.
     * 
     * Fluxo:
     * 1. Cria MimeMessage com jakarta.mail
     * 2. Converte para base64url (formato exigido pela Gmail API)
     * 3. Envia via Gmail API usando "me" como userId (conta autenticada)
     */
    private void sendEmail(String to, String subject, String text) {
        if (gmailService == null) {
            LOGGER.error("[{}] Gmail API não inicializada! Email não enviado para: {}", 
                    Thread.currentThread().getName(), to);
            return;
        }

        try {
            LOGGER.info("[{}] Enviando email via Gmail API para: {}", 
                    Thread.currentThread().getName(), to);
            LOGGER.info("[{}] From: {}, Subject: {}", 
                    Thread.currentThread().getName(), fromEmail, subject);

            // 1. Cria MimeMessage
            MimeMessage mimeMessage = createMimeMessage(to, subject, text);
            
            // 2. Converte para base64url
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            mimeMessage.writeTo(buffer);
            String encodedEmail = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(buffer.toByteArray());
            
            // 3. Cria Message da Gmail API
            Message message = new Message();
            message.setRaw(encodedEmail);
            
            // 4. Envia via Gmail API
            // "me" = conta autenticada pelo OAuth
            Message sentMessage = gmailService.users().messages()
                    .send("me", message)
                    .execute();
            
            LOGGER.info("[{}] Email enviado com SUCESSO via Gmail API! MessageId: {}", 
                    Thread.currentThread().getName(), sentMessage.getId());

        } catch (MessagingException | IOException e) {
            LOGGER.error("[{}] ERRO ao enviar email via Gmail API para {}: {}", 
                    Thread.currentThread().getName(), to, e.getMessage(), e);
        }
    }

    /**
     * Cria MimeMessage para envio.
     */
    private MimeMessage createMimeMessage(String to, String subject, String text) 
            throws MessagingException {
        
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(fromEmail));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject, "UTF-8");
        email.setText(text, "UTF-8");
        
        return email;
    }
}
