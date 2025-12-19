package com.cptrans.petrocarga.services;

import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setText(htmlBody, true);
            helper.setTo(to);
            helper.setSubject(subject);
            // From is sourced from spring.mail.username if configured
            mailSender.send(message);
            System.out.println("Email HTML enviado para: " + to + " assunto: " + subject);
        } catch (Exception e) {
            System.err.println("Falha ao enviar email HTML para " + to + ": " + e.getMessage());
        }
    }

    public void sendPlainTextEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            System.out.println("Email enviado para: " + to + " assunto: " + subject);
        } catch (Exception e) {
            System.err.println("Falha ao enviar email para " + to + ": " + e.getMessage());
        }
    }

    /**
     * Template HTML básico com CSS inline para deixar o email apresentável.
     */
    public String buildHtmlTemplate(String title, String bodyHtml) {
        return "<!doctype html>" +
                "<html><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">" +
                "<style>body{font-family:Arial,Helvetica,sans-serif;background:#f5f7fa;margin:0;padding:0} .container{max-width:600px;margin:30px auto;background:#ffffff;padding:20px;border-radius:8px;box-shadow:0 2px 6px rgba(0,0,0,0.08)} h1{color:#333;font-size:20px} p{color:#555;line-height:1.5} .button{display:inline-block;padding:10px 16px;background:#007bff;color:#fff;border-radius:6px;text-decoration:none}</style></head>" +
                "<body><div class=\"container\"><h1>" + title + "</h1><div>" + bodyHtml + "</div></div></body></html>";
    }
}
