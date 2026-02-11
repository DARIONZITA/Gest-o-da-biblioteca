package com.example.bibliotecaapi.service;

import com.example.bibliotecaapi.model.StatusUsuario;
import com.example.bibliotecaapi.model.Usuario;
import com.example.bibliotecaapi.repository.UsuarioRepository;
import com.example.bibliotecaapi.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResult login(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas"));

        if (usuario.getStatus() == StatusUsuario.BLOQUEADO) {
            throw new RuntimeException("Usuário bloqueado");
        }

        if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            throw new RuntimeException("Usuário sem senha cadastrada");
        }

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        String token = jwtUtil.generateToken(usuario);
        return new LoginResult(token, usuario);
    }

    public record LoginResult(String token, Usuario usuario) {
    }
}
