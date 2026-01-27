package com.cptrans.petrocarga.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@Profile("!local-noemail")
public class MailConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailConfig.class);

    @Value("${SMTP_HOST:}")
    private String host;

    @Value("${SMTP_PORT:25}")
    private int port;

    @Value("${SMTP_USERNAME:}")
    private String username;

    @Value("${SMTP_PASSWORD:}")
    private String password;

    @Value("${SMTP_FROM:}")
    private String smtpFrom;

    @Value("${SMTP_STARTTLS:false}")
    private boolean starttls;

    @Value("${SMTP_SSL:false}")
    private boolean ssl;

    @Value("${SMTP_TIMEOUT:10000}")
    private int timeoutMillis;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl impl = new JavaMailSenderImpl();

        if (host == null || host.isBlank()) {
            LOGGER.warn("SMTP_HOST is not set. JavaMailSender will be created but sending will likely fail.");
        } else {
            impl.setHost(host);
        }

        impl.setPort(port);

        if (username != null && !username.isBlank()) {
            impl.setUsername(username);
        }
        if (password != null && !password.isBlank()) {
            impl.setPassword(password);
        }

        Properties props = impl.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", (username != null && !username.isBlank()) ? "true" : "false");
        props.put("mail.smtp.starttls.enable", Boolean.toString(starttls));
        props.put("mail.smtp.ssl.enable", Boolean.toString(ssl));

        // Timeouts to avoid indefinite blocking in cloud environments
        props.put("mail.smtp.connectiontimeout", Integer.toString(timeoutMillis));
        props.put("mail.smtp.timeout", Integer.toString(timeoutMillis));
        props.put("mail.smtp.writetimeout", Integer.toString(timeoutMillis));

        LOGGER.info("Configured JavaMailSender - host={}, port={}, userPresent={}, starttls={}, ssl={}, timeoutMs={}",
                host, port, (username != null && !username.isBlank()), starttls, ssl, timeoutMillis);

        // Note: 'from' concept is handled by application properties / EmailService
        return impl;
    }
}
