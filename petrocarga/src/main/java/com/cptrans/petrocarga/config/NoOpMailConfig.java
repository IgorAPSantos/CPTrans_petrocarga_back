package com.cptrans.petrocarga.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;

@Configuration
@Profile("local")
public class NoOpMailConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpMailConfig.class);

    @Bean
    public JavaMailSender javaMailSender() {
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
                LOGGER.info("[NO-OP MAIL] would send MimeMessage to: {}", (Object) mimeMessage.getAllRecipients());
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
