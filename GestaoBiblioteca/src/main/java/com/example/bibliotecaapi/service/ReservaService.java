package com.example.bibliotecaapi.service;

import com.example.bibliotecaapi.model.Emprestimo;
import com.example.bibliotecaapi.model.Livro;
import com.example.bibliotecaapi.model.Reserva;
import com.example.bibliotecaapi.model.StatusReserva;
import com.example.bibliotecaapi.model.StatusUsuario;
import com.example.bibliotecaapi.repository.EmprestimoRepository;
import com.example.bibliotecaapi.repository.ReservaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReservaService {
    private final ReservaRepository repository;
    private final EmprestimoRepository emprestimoRepository;
    private final LivroService livroService;

    public ReservaService(ReservaRepository repository,EmprestimoRepository emprestimoRepository, LivroService livroService) {
        this.livroService = livroService;
        this.repository = repository;
        this.emprestimoRepository = emprestimoRepository;
    }

    public Reserva salvar(Reserva reserva) {
        // Validação: Usuário bloqueado não pode realizar reservas
        if (reserva.getUsuario().getStatus() == StatusUsuario.BLOQUEADO) {
            throw new RuntimeException("Usuário bloqueado não pode realizar reservas!");
        }
        
        // 1. Validar se o usuário tem livros atrasados
        boolean temAtraso = emprestimoRepository.existeAtrasoAtivo(
                reserva.getUsuario().getId(),
                LocalDate.now()
        );

        if(temAtraso){
            throw new RuntimeException("Este usuário tem multas por pagar e não pode realizar reservas");
        }

        // 2. Validar se o livro já está reservado pelo mesmo usuário
        if (repository.existsByUsuarioAndLivroAndStatus((reserva.getUsuario()), reserva.getLivro(), StatusReserva.ATIVA)) {
            throw new RuntimeException("Este usuário já tem uma reserva ativa para este livro!");
        }

        // 3. VALIDAÇÃO: Se houver livros disponíveis, não faz sentido reservar
        if (reserva.getLivro().getQtdDisponivel() > 0) {
            throw new RuntimeException("Reserva negada: Existem exemplares disponíveis na prateleira. Faça um empréstimo direto.");
        }
        
        // 4. CÁLCULO AUTOMÁTICO DA POSIÇÃO NA FILA
        // Conta quantas reservas ativas já existem para este livro
        Integer reservasAtivas = repository.countByLivroIdAndStatus(
                reserva.getLivro().getId(), 
                StatusReserva.ATIVA
        );
        
        // A nova reserva será a próxima na fila (count + 1)
        reserva.setPosicaoFila(reservasAtivas + 1);
        reserva.setDataReserva(LocalDate.now());
        reserva.setStatus(StatusReserva.ATIVA);

        return repository.save(reserva);
    }

    public List<Reserva> listarTodas() {
        return repository.findAll();
    }

    public List<Reserva> listarPorUsuario(UUID usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    public Optional<Reserva> buscarPorId(UUID id) {
        return repository.findById(id);
    }
    @Transactional
    public Reserva cancelar(UUID id) {
        // 1. BUSCA: Localizamos a reserva pela chave primária R.id
        Reserva reserva = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada!"));

        // 2. VALIDAÇÃO: Só podemos cancelar reservas que ainda estão PENDENTES ou ATIVAS
        // Se já foi CANCELADA ou FINALIZADA, não fazemos nada
        if (reserva.getStatus() != StatusReserva.ATIVA) {
            throw new RuntimeException("Esta reserva não pode ser cancelada pois o status atual é: " + reserva.getStatus());
        }

        // 3. ATUALIZAÇÃO DE ESTADO: Mudamos o status para CANCELADA
        reserva.setStatus(StatusReserva.CANCELADA);

        // Opcional: Registar a data do cancelamento (Chrono Time)
        // reserva.setDataCancelamento(LocalDate.now());

        // 4. PERSISTÊNCIA: O save() aqui faz o UPDATE na base de dados
        return repository.save(reserva);
    }

    public void deletar(UUID id) {
        repository.deleteById(id);
    }
}
