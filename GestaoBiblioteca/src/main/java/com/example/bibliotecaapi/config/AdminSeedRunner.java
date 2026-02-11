package com.example.bibliotecaapi.config;

import com.example.bibliotecaapi.model.PerfilUsuario;
import com.example.bibliotecaapi.model.StatusUsuario;
import com.example.bibliotecaapi.model.Usuario;
import com.example.bibliotecaapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeedRunner implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private final boolean enabled;
    private final String nome;
    private final String email;
    private final String senha;
    private final Integer matricula;

    public AdminSeedRunner(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed.admin.enabled:true}") boolean enabled,
            @Value("${app.seed.admin.nome:}") String nome,
            @Value("${app.seed.admin.email:}") String email,
            @Value("${app.seed.admin.senha:}") String senha,
            @Value("${app.seed.admin.matricula:}") Integer matricula
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.matricula = matricula;
    }

    @Override
    public void run(String... args) {
        if (!enabled) {
            return;
        }

        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            return;
        }

        if (matricula == null || matricula <= 0) {
            return;
        }

        if (usuarioRepository.findByEmail(email).isPresent()) {
            return;
        }

        if (usuarioRepository.findByMatricula(matricula).isPresent()) {
            return;
        }

        String nomeFinal = (nome == null || nome.isBlank()) ? "Administrador" : nome;

        Usuario admin = Usuario.builder()
                .matricula(matricula)
                .nome(nomeFinal)
                .email(email)
                .senha(passwordEncoder.encode(senha))
                .perfil(PerfilUsuario.ADMIN)
                .status(StatusUsuario.ATIVO)
                .build();

        usuarioRepository.save(admin);
    }
}
