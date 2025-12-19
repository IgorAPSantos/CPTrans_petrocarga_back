package com.cptrans.petrocarga.services;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cptrans.petrocarga.dto.AuthRequestDTO;
import com.cptrans.petrocarga.dto.AuthResponseDTO;
import com.cptrans.petrocarga.models.TokenConfirmacao;
import com.cptrans.petrocarga.models.TokenRecuperacao;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.repositories.TokenConfirmacaoRepository;
import com.cptrans.petrocarga.repositories.TokenRecuperacaoRepository;
import com.cptrans.petrocarga.repositories.UsuarioRepository;
import com.cptrans.petrocarga.security.JwtService;
import com.cptrans.petrocarga.services.EmailService;

@Service
public class AuthService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenConfirmacaoRepository tokenConfirmacaoRepository;

    @Autowired
    private TokenRecuperacaoRepository tokenRecuperacaoRepository;

    @Autowired
    private EmailService emailService;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String CODE_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int DEFAULT_PASSWORD_LENGTH = 12;
    private static final Locale LOCALE = Locale.getDefault();

    public AuthResponseDTO login(AuthRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas."));
        System.out.println("Usuario encontrado: " + usuario.getEmail());
        if(usuario.getAtivo() == false) {
            throw new IllegalArgumentException("Usuário desativado.");
        }
        if(!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new IllegalArgumentException("Credenciais inválidas.");
        }
        String token = jwtService.gerarToken(usuario);

       return new AuthResponseDTO(usuario.toResponseDTO(), token);
    }

    /**
     * Envia email de confirmação com token (validade 24h)
     */
    @Transactional
    public void enviarConfirmacao(Usuario usuario) {
        Objects.requireNonNull(usuario, "usuario não pode ser nulo");
        String token = UUID.randomUUID().toString();
        OffsetDateTime expiracao = OffsetDateTime.now().plusHours(24);
        TokenConfirmacao tc = new TokenConfirmacao(token, expiracao, usuario);
        tokenConfirmacaoRepository.save(tc);

        String link = "http://exemplo-app.com/confirmar?token=" + token;
        String body = "<p>Olá " + usuario.getNome() + ",</p>" +
                      "<p>Por favor confirme sua conta clicando no botão abaixo. O link expira em 24 horas.</p>" +
                      "<p><a href=\"" + link + "\" class=\"button\">Confirmar conta</a></p>";
        String html = emailService.buildHtmlTemplate("Confirme sua conta", body);
        emailService.sendHtmlEmail(usuario.getEmail(), "Confirmação de conta", html);
    }

    /**
     * Confirma conta usando token: ativa usuário, gera senha aleatória, envia por email e remove token
     */
    @Transactional
    public void confirmarConta(String token) {
        TokenConfirmacao tc = tokenConfirmacaoRepository.findByToken(token).orElseThrow(() -> new IllegalArgumentException("Token inválido."));
        if (OffsetDateTime.now().isAfter(tc.getDataExpiracao())) {
            tokenConfirmacaoRepository.deleteByToken(token);
            throw new IllegalArgumentException("Token expirado.");
        }

        Usuario usuario = tc.getUsuario();
        usuario.setAtivo(true);
        String senhaGerada = generateRandomPassword(DEFAULT_PASSWORD_LENGTH);
        usuario.setSenha(passwordEncoder.encode(senhaGerada));
        usuarioRepository.save(usuario);

        tokenConfirmacaoRepository.deleteByToken(token);

        String body = "Olá " + usuario.getNome() + ",<br/><br/>" +
                      "Sua conta foi confirmada. Sua senha temporária é:<br/><br/>" +
                      "<strong>" + senhaGerada + "</strong><br/><br/>" +
                      "Recomendamos que altere a senha após o primeiro login.";
        emailService.sendPlainTextEmail(usuario.getEmail(), "Senha de acesso", body);
    }

    /**
     * Solicita recuperação: gera código 6 dígitos alfa-numérico (valido 10 minutos) e envia por email HTML
     */
    @Transactional
    public void solicitarRecuperacao(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        String codigo = generateCode(6);
        OffsetDateTime expiracao = OffsetDateTime.now().plusMinutes(10);
        TokenRecuperacao tr = new TokenRecuperacao(codigo, expiracao, usuario);
        tokenRecuperacaoRepository.save(tr);

        String body = "<p>Olá " + usuario.getNome() + ",</p>" +
                      "<p>Recebemos uma solicitação de recuperação de senha. Use o código abaixo para validar a operação. O código expira em 10 minutos.</p>" +
                      "<p style=\"font-size:20px;font-weight:bold;\">" + codigo + "</p>";
        String html = emailService.buildHtmlTemplate("Recuperação de senha", body);
        emailService.sendHtmlEmail(usuario.getEmail(), "Código de recuperação", html);
    }

    /**
     * Valida código e altera senha
     */
    @Transactional
    public void validarEAlterarSenha(String email, String codigo, String novaSenha) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        TokenRecuperacao tr = tokenRecuperacaoRepository.findByCodigoAndUsuarioAndUtilizadoFalse(codigo, usuario).orElseThrow(() -> new IllegalArgumentException("Código inválido ou já utilizado."));
        if (OffsetDateTime.now().isAfter(tr.getDataExpiracao())) {
            throw new IllegalArgumentException("Código expirado.");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        tr.setUtilizado(true);
        tokenRecuperacaoRepository.save(tr);

        String body = "Olá " + usuario.getNome() + ",\n\nSua senha foi alterada com sucesso.";
        emailService.sendPlainTextEmail(usuario.getEmail(), "Senha alterada", body);
    }

    /* Helpers */
    private String generateCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = secureRandom.nextInt(CODE_CHARSET.length());
            sb.append(CODE_CHARSET.charAt(idx));
        }
        return sb.toString();
    }

    private String generateRandomPassword(int length) {
        // Use alphanumeric + punctuation
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_+=";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = secureRandom.nextInt(chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }
}
