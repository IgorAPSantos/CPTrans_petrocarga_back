package com.cptrans.petrocarga.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;

/**
 * Fallback No-Op JavaMailSender.
 * Ativado automaticamente quando nenhum outro JavaMailSender bean existe
 * (ex: SMTP_HOST não definido). Não envia emails de verdade, apenas loga.
 */
@Configuration
public class NoOpMailConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpMailConfig.class);

    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender noOpJavaMailSender() {
        LOGGER.warn("========================================================");
        LOGGER.warn("  SMTP_HOST not configured. Using NO-OP Mail Sender.");
        LOGGER.warn("  Emails will NOT be sent. Configure SMTP_HOST to enable.");
        LOGGER.warn("========================================================");
        return new JavaMailSender() {
            private Session session = Session.getDefaultInstance(new Properties());

            @Override
            public MimeMessage createMimeMessage() {
                return new MimeMessage(session);
            }

            @Override
            public MimeMessage createMimeMessage(InputStream contentStream) {
                try {
                    return new MimeMessage(session, contentStream);
                } catch (Exception e) {
                    LOGGER.warn("Failed to create MimeMessage from stream", e);
                    return createMimeMessage();
                }
            }

            @Override
            public void send(MimeMessage mimeMessage) throws MailException {
                try {
                    LOGGER.info("[NO-OP MAIL] would send MimeMessage to: {}", (Object) mimeMessage.getAllRecipients());
                } catch (Exception e) {
                    LOGGER.info("[NO-OP MAIL] would send MimeMessage (recipients unavailable)");
                }
            }

            @Override
            public void send(MimeMessage... mimeMessages) throws MailException {
                LOGGER.info("[NO-OP MAIL] would send {} MimeMessages", mimeMessages.length);
            }

            @Override
            public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
                LOGGER.info("[NO-OP MAIL] would prepare+send a MimeMessage");
            }

            @Override
            public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
                LOGGER.info("[NO-OP MAIL] would prepare+send {} MimeMessages", mimeMessagePreparators.length);
            }

            @Override
            public void send(SimpleMailMessage simpleMessage) throws MailException {
                LOGGER.info("[NO-OP MAIL] to={} subject={} text={}", simpleMessage.getTo(), simpleMessage.getSubject(), simpleMessage.getText());
            }

            @Override
            public void send(SimpleMailMessage... simpleMessages) throws MailException {
                LOGGER.info("[NO-OP MAIL] would send {} SimpleMailMessage(s)", simpleMessages.length);
            }
        };
    }
}
