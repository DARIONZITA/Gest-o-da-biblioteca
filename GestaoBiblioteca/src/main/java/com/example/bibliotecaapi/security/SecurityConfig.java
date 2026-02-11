package com.example.bibliotecaapi.security;

import com.example.bibliotecaapi.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:5173}") String allowedOrigins
    ) {
        CorsConfiguration config = new CorsConfiguration();

        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtUtil jwtUtil,
            UsuarioRepository usuarioRepository
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/auth/**",
                        // Com server.servlet.context-path=/api, os matchers aqui NÃO incluem o /api
                        "/uploads/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/error",
                        "/recomendacoes/populares"  // Endpoint público de livros populares
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthFilter(jwtUtil, usuarioRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static class JwtAuthFilter extends OncePerRequestFilter {

        private final JwtUtil jwtUtil;
        private final UsuarioRepository usuarioRepository;

        private JwtAuthFilter(JwtUtil jwtUtil, UsuarioRepository usuarioRepository) {
            this.jwtUtil = jwtUtil;
            this.usuarioRepository = usuarioRepository;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (header == null || !header.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = header.substring("Bearer ".length()).trim();
            if (token.isEmpty() || !jwtUtil.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtUtil.extractEmail(token);
            if (email == null || email.isBlank()) {
                filterChain.doFilter(request, response);
                return;
            }

            usuarioRepository.findByEmail(email).ifPresent(usuario -> {
                String perfil = usuario.getPerfil() == null ? null : usuario.getPerfil().name();
                List<GrantedAuthority> authorities = perfil == null
                        ? List.of()
                        : List.of(new SimpleGrantedAuthority("ROLE_" + perfil));

                // Passando o objeto Usuario completo como principal (necessário para RecomendacaoController)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        usuario,  // Usuario completo em vez de apenas email
                        null,
                        authorities
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });

            filterChain.doFilter(request, response);
        }
    }
}
