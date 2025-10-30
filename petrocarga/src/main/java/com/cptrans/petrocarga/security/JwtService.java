package com.cptrans.petrocarga.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.models.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String gerarToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", usuario.getId().toString());
        claims.put("nome", usuario.getNome());
        claims.put("email", usuario.getEmail());
        claims.put("permissao", usuario.getPermissao().name());

        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expirationMs);

        return Jwts.builder()
                .claims(claims) 
                .subject(usuario.getEmail())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean validarToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getEmailDoToken(String token) {
        return getClaims(token).getSubject();
    }

    public UUID getIdDoToken(String token) {
        return UUID.fromString(getClaims(token).get("id").toString());
    }
}