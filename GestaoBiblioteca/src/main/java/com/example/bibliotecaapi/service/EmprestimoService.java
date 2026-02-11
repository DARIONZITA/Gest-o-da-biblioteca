package com.example.bibliotecaapi.service;

import com.example.bibliotecaapi.model.Emprestimo;
import com.example.bibliotecaapi.model.StatusEmprestimo;
import com.example.bibliotecaapi.model.StatusReserva;
import com.example.bibliotecaapi.model.StatusUsuario;
import com.example.bibliotecaapi.repository.EmprestimoRepository;
import com.example.bibliotecaapi.repository.ReservaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.chrono.ChronoLocalDate;

@Transactional
@Service
public class EmprestimoService {
    private final EmprestimoRepository repository;
    private final LivroService livroService;
    private ReservaRepository reservaRepository;

    public EmprestimoService(EmprestimoRepository repository, LivroService livroService,
            ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
        this.repository = repository;
        this.livroService = livroService;
    }

    public Emprestimo salvar(Emprestimo emprestimo) {
        validarEmprestimo(emprestimo);

        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        if (emprestimo.getDataEmprestimo() == null) {
            emprestimo.setDataEmprestimo(LocalDate.now());
        }

        livroService.baixarEstoque(emprestimo.getLivro().getId());
        return repository.save(emprestimo);
    }

    public Emprestimo criarRascunho(Emprestimo emprestimo) {
        validarEmprestimo(emprestimo);

        boolean existeEmprestimo = repository.existsByUsuarioIdAndLivroIdAndStatusIn(
                emprestimo.getUsuario().getId(),
                emprestimo.getLivro().getId(),
                List.of(StatusEmprestimo.PENDENTE, StatusEmprestimo.ATIVO, StatusEmprestimo.ATRASADO));

        if (existeEmprestimo) {
            throw new RuntimeException("O usuário já possui um empréstimo pendente ou ativo deste livro!");
        }

        emprestimo.setStatus(StatusEmprestimo.PENDENTE);
        return repository.save(emprestimo);
    }

    public Emprestimo aprovarRascunho(UUID id) {
        Emprestimo emprestimo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado!"));

        if (emprestimo.getStatus() != StatusEmprestimo.PENDENTE) {
            throw new RuntimeException("Apenas empréstimos pendentes podem ser aprovados.");
        }

        validarEmprestimo(emprestimo);

        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        emprestimo.setDataEmprestimo(LocalDate.now());

        livroService.baixarEstoque(emprestimo.getLivro().getId());
        return repository.save(emprestimo);
    }

    public void validarEmprestimoPendente(UUID usuarioId, UUID livroId) {
        boolean pendente = repository.existsByUsuarioIdAndLivroIdAndDataDevolucaoRealIsNull(usuarioId, livroId);

        if (pendente) {
            throw new RuntimeException("O usuário já possui um empréstimo ativo deste livro!");
        }
    }

    public List<Emprestimo> listarTodos() {
        List<Emprestimo> lista = repository.findAll();

        // 2. Filtramos e atualizamos apenas os que realmente precisam (Performance!)
        lista.stream()
                .filter(e -> e.getStatus() == StatusEmprestimo.ATIVO || e.getStatus() == StatusEmprestimo.ATRASADO)
                .filter(e -> e.getDataDevolucaoReal() == null)
                .forEach(this::atualizarMultaSeNecessario); // Método que já tem o repository.save()

        // 3. Retornamos a lista com os dados já persistidos na base
        return lista;
    }

    public List<Emprestimo> listarPorUsuario(UUID usuarioId) {
        List<Emprestimo> lista = repository.findByUsuarioId(usuarioId);
        lista.stream()
                .filter(e -> e.getStatus() == StatusEmprestimo.ATIVO || e.getStatus() == StatusEmprestimo.ATRASADO)
                .filter(e -> e.getDataDevolucaoReal() == null)
                .forEach(this::atualizarMultaSeNecessario);
        return lista;
    }

    public Optional<Emprestimo> buscarPorId(UUID id) {
        Optional<Emprestimo> emprestimoOpt = repository.findById(id);

        // 2. Usamos o ifPresent para atualizar a multa apenas se o registro existir
        // Isso substitui a necessidade de um if manual com isPresent()
        emprestimoOpt.ifPresent(this::atualizarMultaSeNecessario);

        // 3. Retornamos o Optional original (agora com o objeto interno atualizado)
        return emprestimoOpt;
    }

    @Transactional
    public Emprestimo devolver(UUID id) {
        // 1. Buscar o empréstimo ou lançar erro se não existir
        Emprestimo emprestimo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado!"));

        // 2. VALIDAÇÃO: Não se pode devolver o que já foi devolvido ou cancelado
        if (emprestimo.getStatus() != StatusEmprestimo.ATIVO && emprestimo.getStatus() != StatusEmprestimo.ATRASADO) {
            throw new RuntimeException("Este empréstimo já não está ativo para devolução!");
        }

        // 3. Registrar a DATA REAL (Chrono Time)
        LocalDate hoje = LocalDate.now();
        emprestimo.setDataDevolucaoReal(hoje);

        // 4. Lógica de ATRASO: Comparar data real com a prevista
        // Usamos o isAfter para verificar se passou do prazo
        if (emprestimo.getDataDevolucaoReal().isAfter(emprestimo.getDataPrevista())) {
            long diasDeAtraso = ChronoUnit.DAYS.between(emprestimo.getDataPrevista(),
                    emprestimo.getDataDevolucaoReal());

            // 3. Chamar o teu MultaService passando os dias como parâmetro
            // Exemplo: multaService.gerarMulta(emprestimo, diasDeAtraso);
            emprestimo.setStatus(StatusEmprestimo.ATRASADO);

            System.out.println("Atraso de " + diasDeAtraso + " dias detectado.");
            emprestimo.setValorMulta(MultaService.multa((int) diasDeAtraso));
            System.out.println("Tem uma multa de " + emprestimo.getValorMulta() + "kZ");

        } else {
            emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
        }

        // 5. Devolver o livro ao estoque (Integridade: L.id = E.livro_id)
        livroService.aumentarEstoque(emprestimo.getLivro().getId());

        return repository.save(emprestimo);
    }

    @Transactional
    public Emprestimo renovar(UUID id) {
        // 1. BUSCA: Localiza o empréstimo pela chave primária
        Emprestimo emprestimo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado!"));

        // 2. VALIDAÇÃO DE STATUS: Só renova se o empréstimo estiver ATIVO
        if (emprestimo.getStatus() != StatusEmprestimo.ATIVO) {
            throw new RuntimeException("Apenas empréstimos ativos podem ser renovados.");
        }

        // 3. VALIDAÇÃO: Verificar se o livro já foi devolvido
        if (emprestimo.getDataDevolucaoReal() != null) {
            throw new RuntimeException("Renovação negada: Este livro já foi devolvido.");
        }

        // 4. VALIDAÇÃO DE ATRASO: Não permitimos renovar se já estiver atrasado
        if (LocalDate.now().isAfter(emprestimo.getDataPrevista())) {
            throw new RuntimeException("Não podes renovar um livro que já está atrasado! Devolve e paga a multa.");
        }

        // 5. LIMITE DE RENOVAÇÕES: Máximo de 2 renovações permitidas
        if (emprestimo.getQtdRenovacoes() >= 2) {
            throw new RuntimeException("Limite máximo de 2 renovações atingido para este livro.");
        }

        // 6. NOVA LÓGICA: Verificar estoque mínimo e fila de reservas
        int estoqueAtual = emprestimo.getLivro().getQtdDisponivel();

        // Se estoque está baixo (< 2), verificar reservas
        if (estoqueAtual < 2) {
            boolean existeReserva = reservaRepository.existsByLivroIdAndStatus(
                    emprestimo.getLivro().getId(),
                    StatusReserva.ATIVA);

            if (existeReserva) {
                // Buscar primeiro da fila
                var primeiroNaFila = reservaRepository.findFirstByLivroIdAndStatusOrderByPosicaoFilaAsc(
                        emprestimo.getLivro().getId(),
                        StatusReserva.ATIVA);

                // Se há alguém na fila e não é o usuário do empréstimo, bloquear
                if (primeiroNaFila.isPresent()) {
                    UUID usuarioPrimeiroFila = primeiroNaFila.get().getUsuario().getId();
                    UUID usuarioEmprestimo = emprestimo.getUsuario().getId();

                    if (!usuarioEmprestimo.equals(usuarioPrimeiroFila)) {
                        throw new RuntimeException(
                                "Renovação negada: Estoque baixo e há outros usuários na fila de reserva.");
                    }
                }
            }
        }

        // 7. ATUALIZAÇÃO DA DATA: Estendemos por mais 7 dias a partir da data prevista
        // atual
        emprestimo.setDataPrevista(emprestimo.getDataPrevista().plusDays(7));

        // Incrementamos o contador de renovações
        emprestimo.setQtdRenovacoes(emprestimo.getQtdRenovacoes() + 1);

        return repository.save(emprestimo);
    }

    @Transactional
    public Emprestimo pagarMulta(UUID id) {
        Emprestimo emprestimo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado!"));

        if (emprestimo.getValorMulta() <= 0) {
            throw new RuntimeException("Este empréstimo não possui multas pendentes.");
        }

        emprestimo.setValorMulta(0.0);
        // Se o livro já foi devolvido, o status pode mudar para DEVOLVIDO (caso
        // estivesse ATRASADO apenas por multa pendente)
        // Mas se ainda não devolveu o livro, continua ATRASADO ou ATIVO dependendo da
        // data.
        // Simplificação solicitada: apenas zerar a multa.

        return repository.save(emprestimo);
    }

    private void atualizarMultaSeNecessario(Emprestimo e) {
        // 1. Só calculamos multa se o empréstimo não foi devolvido e já passou da data
        // prevista
        LocalDate hoje = LocalDate.now();

        if ((e.getStatus() == StatusEmprestimo.ATIVO || e.getStatus() == StatusEmprestimo.ATRASADO)
                && e.getDataDevolucaoReal() == null
                && hoje.isAfter(e.getDataPrevista())) {
            // 2. Calcular dias de atraso (Chrono Time)
            long diasAtraso = ChronoUnit.DAYS.between(e.getDataPrevista(), hoje);

            // 3. Atualizar status e calcular multa
            e.setStatus(StatusEmprestimo.ATRASADO);
            e.setValorMulta(MultaService.multa((int) diasAtraso));

            // 4. Salvar a alteração na base de dados (Update E.id)
            repository.save(e);
        }
    }

    private void validarEmprestimo(Emprestimo emprestimo) {
        // Validação: Usuário bloqueado não pode realizar empréstimos
        if (emprestimo.getUsuario().getStatus() == StatusUsuario.BLOQUEADO) {
            throw new RuntimeException("Usuário bloqueado não pode realizar empréstimos!");
        }

        LocalDate hoje = LocalDate.now();
        if (emprestimo.getDataPrevista().isBefore(hoje)) {
            throw new RuntimeException("A data prevista de devolução não pode ser anterior à data de hoje!");
        }

        // Definimos o limite máximo permitido (Hoje + 4 dias)
        LocalDate dataLimite = hoje.plusDays(4);

        // VALIDAÇÃO CRONOLÓGICA: Prazo máximo de 4 dias
        if (emprestimo.getDataPrevista().isAfter(dataLimite)) {
            throw new RuntimeException("Erro: O prazo máximo para devolução é de apenas 4 dias.");
        }

        boolean existeReserva = reservaRepository.existsByLivroIdAndStatus(emprestimo.getLivro().getId(),
                StatusReserva.ATIVA);

        if (existeReserva) {
            throw new RuntimeException(
                    "Empréstimo negado: Este livro possui reservas pendentes. Prioridade para quem reservou!");
        }

        if (emprestimo.getLivro().getQtdDisponivel() <= 0) {
            throw new RuntimeException("Não é possível realizar o empréstimo, o estoque para este livro está esgotado");
        }
    }
}
