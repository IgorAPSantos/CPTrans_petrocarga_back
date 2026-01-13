package com.cptrans.petrocarga.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptrans.petrocarga.dto.AuthRequestDTO;
import com.cptrans.petrocarga.dto.AuthResponseDTO;
import com.cptrans.petrocarga.dto.UsuarioRequestDTO;
import com.cptrans.petrocarga.dto.UsuarioResponseDTO;
import com.cptrans.petrocarga.dto.AccountActivationRequest;
import com.cptrans.petrocarga.dto.ResendCodeRequest;
import com.cptrans.petrocarga.dto.ForgotPasswordRequest;
import com.cptrans.petrocarga.dto.ResetPasswordRequest;
import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.security.UserAuthenticated;
import com.cptrans.petrocarga.services.AuthService;
import com.cptrans.petrocarga.services.UsuarioService;

import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // TODO: Remover instância depois de cadastrar o primeiro admin em deploy
    @Autowired
    private UsuarioService usuarioService;
    
    @Value("${app.cookie-settings.secure:true}")
    private boolean secure;

    @Value("${app.cookie-settings.same-site:None}")
    private String sameSite;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request, HttpServletResponse response) {
        AuthResponseDTO auth = authService.login(request);
        ResponseCookie cookie = ResponseCookie.from("auth-token", auth.getToken())
            .httpOnly(true)
            .secure(secure)
            .sameSite(sameSite)
            .path("/")
            .maxAge(java.time.Duration.ofHours(2))
            .build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(auth);
    }

    //TODO: Remover rota depois de cadastrar o primeiro admin em deploy
    @PostMapping("/admin")
    public ResponseEntity<UsuarioResponseDTO> createAdmin(@RequestBody UsuarioRequestDTO usuario) {
        Usuario novoUsuario = usuarioService.createUsuario(usuario.toEntity(), PermissaoEnum.ADMIN);
        return ResponseEntity.ok(novoUsuario.toResponseDTO());
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> getMe(){
        UUID usuarioIdFromToken = (((UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).id());
        Usuario usuarioLogado = usuarioService.findByIdAndAtivo(usuarioIdFromToken, true);
        return ResponseEntity.ok(usuarioLogado.toResponseDTO());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("auth-token", "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
   }

        @PostMapping("/activate")
        public ResponseEntity<Void> activateAccount(@RequestBody AccountActivationRequest request) {
            try {
                usuarioService.activateAccount(request.email(), request.code());
                return ResponseEntity.ok().build();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            } catch (jakarta.persistence.EntityNotFoundException e) {
                return ResponseEntity.notFound().build();
            }
        }

        @PostMapping("/resend-code")
        public ResponseEntity<Void> resendCode(@RequestBody ResendCodeRequest request) {
            try {
                usuarioService.resendActivationCode(request.email());
                return ResponseEntity.ok().build();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(null);
            } catch (jakarta.persistence.EntityNotFoundException e) {
                return ResponseEntity.notFound().build();
            }
        }

        @PostMapping("/forgot-password")
        public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
            try {
                usuarioService.forgotPassword(request.email());
                return ResponseEntity.ok().build();
            } catch (jakarta.persistence.EntityNotFoundException e) {
                // Retorna OK mesmo se não encontrar para não expor se o email existe
                return ResponseEntity.ok().build();
            }
        }

        @PostMapping("/reset-password")
        public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
            try {
                usuarioService.resetPassword(request.email(), request.code(), request.novaSenha());
                return ResponseEntity.ok().build();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            } catch (jakarta.persistence.EntityNotFoundException e) {
                return ResponseEntity.notFound().build();
            }
        }
}