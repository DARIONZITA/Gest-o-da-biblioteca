package com.example.bibliotecaapi.controller;

import com.example.bibliotecaapi.model.Usuario;
import com.example.bibliotecaapi.security.JwtUtil;
import com.example.bibliotecaapi.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthService.LoginResult result = authService.login(request.email(), request.senha());
        Usuario usuario = result.usuario();

        Date exp = jwtUtil.extractExpiration(result.token());

        return ResponseEntity.ok(new LoginResponse(
                result.token(),
                "Bearer",
                exp == null ? null : exp.getTime(),
                new UsuarioResumo(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getPerfil() == null ? null : usuario.getPerfil().name()
                )
        ));
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String senha
    ) {
    }

    public record UsuarioResumo(
            UUID id,
            String nome,
            String email,
            String perfil
    ) {
    }

    public record LoginResponse(
            String token,
            String tokenType,
            Long expiresAt,
            UsuarioResumo usuario
    ) {
    }
}
