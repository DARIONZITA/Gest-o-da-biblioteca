package com.example.bibliotecaapi.security;

import com.example.bibliotecaapi.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtUtil(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-ms}") long expirationMs
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("security.jwt.secret não pode ser vazio");
        }
        // HS256 exige pelo menos 256 bits (~32 chars) de chave.
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("security.jwt.secret deve ter pelo menos 32 bytes (HS256)");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(Usuario usuario) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);

        Map<String, Object> claims = Map.of(
                "uid", usuario.getId() == null ? null : usuario.getId().toString(),
                "role", usuario.getPerfil() == null ? null : usuario.getPerfil().name()
        );

        return Jwts.builder()
                .subject(usuario.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claims(claims)
                .signWith(secretKey)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        Object role = parseClaims(token).get("role");
        return role == null ? null : role.toString();
    }

    public Date extractExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            Date exp = claims.getExpiration();
            return exp != null && exp.after(new Date());
        } catch (Exception ex) {
            return false;
        }
    }
}
