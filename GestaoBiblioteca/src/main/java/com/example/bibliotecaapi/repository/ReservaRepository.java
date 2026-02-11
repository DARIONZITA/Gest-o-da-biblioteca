package com.example.bibliotecaapi.repository;

import com.example.bibliotecaapi.model.Livro;
import com.example.bibliotecaapi.model.Reserva;
import com.example.bibliotecaapi.model.StatusReserva;
import com.example.bibliotecaapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
    java.util.List<Reserva> findByUsuarioId(UUID usuarioId);

    boolean existsByUsuarioAndLivroAndStatus(Usuario usuario, Livro livro, StatusReserva status);
    boolean existsByLivroIdAndStatus(UUID livroId, StatusReserva status);
    
    // Busca a primeira reserva na fila (menor posicaoFila) para um livro específico
    Optional<Reserva> findFirstByLivroIdAndStatusOrderByPosicaoFilaAsc(UUID livroId, StatusReserva status);
    
    // Conta quantas reservas ativas existem para um livro (para calcular próxima posição)
    Integer countByLivroIdAndStatus(UUID livroId, StatusReserva status);
}