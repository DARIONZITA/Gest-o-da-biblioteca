package com.example.bibliotecaapi.config;

import com.example.bibliotecaapi.model.*;
import com.example.bibliotecaapi.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Seed runner para dados de apresentação do projeto.
 * Cria categorias, autores, editoras, livros, utilizadores, empréstimos e
 * reservas.
 * Ativado pela propriedade app.seed.demo.enabled=true
 */
@Component
@Order(2) // Executa após AdminSeedRunner
@Slf4j
public class DemoSeedRunner implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoriaRepository categoriaRepository;
    private final AutorRepository autorRepository;
    private final EditoraRepository editoraRepository;
    private final LivroRepository livroRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final ReservaRepository reservaRepository;

    @Value("${app.seed.demo.enabled:false}")
    private boolean enabled;

    public DemoSeedRunner(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            CategoriaRepository categoriaRepository,
            AutorRepository autorRepository,
            EditoraRepository editoraRepository,
            LivroRepository livroRepository,
            EmprestimoRepository emprestimoRepository,
            ReservaRepository reservaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.categoriaRepository = categoriaRepository;
        this.autorRepository = autorRepository;
        this.editoraRepository = editoraRepository;
        this.livroRepository = livroRepository;
        this.emprestimoRepository = emprestimoRepository;
        this.reservaRepository = reservaRepository;
    }

    @Override
    public void run(String... args) {
        if (!enabled) {
            log.info("Demo seed desativado. Para ativar: app.seed.demo.enabled=true");
            return;
        }

        // Verificar se já existem dados demo (evitar duplicatas)
        if (usuarioRepository.findByEmail("20260101@isptec.co.ao").isPresent()) {
            log.info("Dados demo já existem. Seed ignorado.");
            return;
        }

        log.info("🌱 Inserindo dados de demonstração para apresentação...");

        // 1. Categorias
        Map<String, Categoria> categorias = criarCategorias();

        // 2. Autores
        Map<String, Autor> autores = criarAutores();

        // 3. Editoras
        Map<String, Editora> editoras = criarEditoras();

        // 4. Livros
        Map<String, Livro> livros = criarLivros(categorias, autores, editoras);

        // 5. Utilizadores
        Map<String, Usuario> usuarios = criarUsuarios();

        // 6. Empréstimos
        criarEmprestimos(usuarios, livros);

        // 7. Reservas
        criarReservas(usuarios, livros);

        log.info("✅ Dados demo inseridos com sucesso!");
        log.info("📊 {} categorias, {} autores, {} editoras, {} livros, {} utilizadores",
                categorias.size(), autores.size(), editoras.size(), livros.size(), usuarios.size());
    }

    // ========================= CATEGORIAS =========================
    private Map<String, Categoria> criarCategorias() {
        Map<String, Categoria> map = new LinkedHashMap<>();
        String[][] cats = {
                { "Programação", "Livros sobre linguagens de programação, algoritmos e desenvolvimento de software" },
                { "Ciência de Dados", "Machine Learning, estatística, análise de dados e IA" },
                { "Redes e Segurança", "Infraestrutura de redes, cibersegurança e protocolos" },
                { "Matemática", "Cálculo, álgebra linear, matemática discreta e aplicada" },
                { "Engenharia de Software", "Padrões de projeto, arquitetura de software e boas práticas" },
                { "Sistemas Operativos", "Linux, Windows, administração de sistemas" },
                { "Base de Dados", "SQL, NoSQL, modelagem e administração de bancos de dados" },
                { "Literatura", "Romances, contos e obras literárias clássicas e contemporâneas" }
        };
        for (String[] c : cats) {
            Categoria cat = categoriaRepository.save(
                    Categoria.builder().nome(c[0]).descricao(c[1]).build());
            map.put(c[0], cat);
        }
        return map;
    }

    // ========================= AUTORES =========================
    private Map<String, Autor> criarAutores() {
        Map<String, Autor> map = new LinkedHashMap<>();
        String[][] autors = {
                { "Robert C. Martin", "Conhecido como Uncle Bob, autor de Clean Code e Clean Architecture",
                        "Americana" },
                { "Martin Fowler", "Especialista em arquitetura de software e refatoração", "Britânica" },
                { "Andrew S. Tanenbaum", "Professor e autor de livros clássicos de redes e sistemas operativos",
                        "Americana" },
                { "Thomas H. Cormen", "Co-autor de Introduction to Algorithms (CLRS)", "Americana" },
                { "Joshua Bloch", "Ex-engenheiro da Google, autor de Effective Java", "Americana" },
                { "Erich Gamma", "Co-autor dos Design Patterns (Gang of Four)", "Suíça" },
                { "Aurélio Marinho Jargas", "Autor brasileiro de livros sobre Shell Script e regex", "Brasileira" },
                { "Pepetela", "Escritor angolano, prémio Camões, autor de Mayombe", "Angolana" },
                { "Chinua Achebe", "Escritor nigeriano, autor de Things Fall Apart", "Nigeriana" },
                { "Wes McKinney", "Criador do Pandas, autor de Python for Data Analysis", "Americana" }
        };
        for (String[] a : autors) {
            Autor autor = Autor.builder().nome(a[0]).descricao(a[1]).nacionalidade(a[2]).build();
            autor = autorRepository.save(autor);
            map.put(a[0], autor);
        }
        return map;
    }

    // ========================= EDITORAS =========================
    private Map<String, Editora> criarEditoras() {
        Map<String, Editora> map = new LinkedHashMap<>();
        String[] eds = { "O'Reilly Media", "Pearson Education", "Addison-Wesley",
                "Novatec Editora", "Editorial Caminho", "McGraw-Hill", "MIT Press" };
        for (String nome : eds) {
            Editora e = new Editora();
            e.setNome(nome);
            e = editoraRepository.save(e);
            map.put(nome, e);
        }
        return map;
    }

    // ========================= LIVROS =========================
    private Map<String, Livro> criarLivros(Map<String, Categoria> cats, Map<String, Autor> auts,
            Map<String, Editora> eds) {
        Map<String, Livro> map = new LinkedHashMap<>();

        // [isbn, titulo, paginas, autor, categoria, editora, ano, qtdTotal,
        // qtdDisponivel, localizacao, sinopse]
        Object[][] livros = {
                { "978-0132350884", "Clean Code", 464, "Robert C. Martin", "Engenharia de Software",
                        "Pearson Education", 2008, 5, 3, "Estante A1",
                        "Even bad code can function. But if code isn't clean, it can bring a development organization to its knees." },
                { "978-0134494166", "Clean Architecture", 432, "Robert C. Martin", "Engenharia de Software",
                        "Pearson Education", 2017, 3, 2, "Estante A1",
                        "Building upon the success of Clean Code, Robert C. Martin reveals architectural rules and practices." },
                { "978-0134757599", "Refactoring", 448, "Martin Fowler", "Engenharia de Software", "Addison-Wesley",
                        2018, 4, 3, "Estante A2",
                        "For more than twenty years, experienced programmers have relied on Refactoring to improve existing code." },
                { "978-8582604274", "Redes de Computadores", 960, "Andrew S. Tanenbaum", "Redes e Segurança",
                        "Pearson Education", 2021, 1, 0, "Estante B1",
                        "A referência completa em redes de computadores. Aborda desde os fundamentos físicos até aplicações modernas." },
                { "978-0262046305", "Introduction to Algorithms", 1312, "Thomas H. Cormen", "Matemática", "MIT Press",
                        2022, 3, 1, "Estante B2",
                        "The bible of algorithms. A comprehensive textbook covering a broad range of algorithms in depth." },
                { "978-0134685991", "Effective Java", 416, "Joshua Bloch", "Programação", "Addison-Wesley", 2018, 4, 2,
                        "Estante A3",
                        "The definitive guide to Java programming language best practices from Joshua Bloch." },
                { "978-0201633610", "Design Patterns", 395, "Erich Gamma", "Engenharia de Software", "Addison-Wesley",
                        1994, 2, 1, "Estante A2",
                        "The classic book on object-oriented design patterns. Captures solutions to recurring problems." },
                { "978-8582606162", "Sistemas Operacionais Modernos", 864, "Andrew S. Tanenbaum", "Sistemas Operativos",
                        "Pearson Education", 2016, 2, 0, "Estante C1",
                        "Cobre todos os aspectos de sistemas operacionais modernos: processos, threads, gestão de memória." },
                { "978-8575221525", "Shell Script Profissional", 480, "Aurélio Marinho Jargas", "Sistemas Operativos",
                        "Novatec Editora", 2008, 3, 3, "Estante C2",
                        "Guia completo de shell script para administradores de sistemas e programadores." },
                { "978-9722121149", "Mayombe", 288, "Pepetela", "Literatura", "Editorial Caminho", 1980, 5, 4,
                        "Estante D1",
                        "Romance de guerra e de amor, relata a luta pela independência de Angola nas florestas do Mayombe." },
                { "978-0385474542", "Things Fall Apart", 209, "Chinua Achebe", "Literatura", "McGraw-Hill", 1958, 3, 2,
                        "Estante D1",
                        "A classic work of African literature about Okonkwo and the arrival of European colonizers." },
                { "978-1098104030", "Python for Data Analysis", 576, "Wes McKinney", "Ciência de Dados",
                        "O'Reilly Media", 2022, 3, 1, "Estante B3",
                        "The definitive handbook for manipulating and processing datasets in Python using pandas." },
                { "978-0135957059", "The Pragmatic Programmer", 352, "Martin Fowler", "Engenharia de Software",
                        "Addison-Wesley", 2019, 3, 2, "Estante A1",
                        "Your journey to mastery. Updated for modern development with expanded topics." },
                { "978-9722105309", "A Geração da Utopia", 320, "Pepetela", "Literatura", "Editorial Caminho", 1992, 4,
                        3, "Estante D2",
                        "Romance que retrata a evolução de estudantes angolanos em Lisboa até à independência de Angola." },
                { "978-9722117968", "Predadores", 448, "Pepetela", "Literatura", "Editorial Caminho", 2005, 3, 3,
                        "Estante D2",
                        "Romance sobre a corrupção em Angola pós-independência através da história de Vladimiro Caposso." }
        };

        for (Object[] l : livros) {
            Livro livro = Livro.builder()
                    .isbn((String) l[0])
                    .titulo((String) l[1])
                    .qtdPaginas((Integer) l[2])
                    .autor(auts.get((String) l[3]))
                    .categoria(cats.get((String) l[4]))
                    .editora(eds.get((String) l[5]))
                    .anoPublicacao((Integer) l[6])
                    .qtdTotal((Integer) l[7])
                    .qtdDisponivel((Integer) l[8])
                    .localizacao((String) l[9])
                    .sinopse((String) l[10])
                    .build();
            livro = livroRepository.save(livro);
            map.put((String) l[0], livro);
        }
        return map;
    }

    // ========================= UTILIZADORES =========================
    private Map<String, Usuario> criarUsuarios() {
        Map<String, Usuario> map = new LinkedHashMap<>();
        String encodedPassword = passwordEncoder.encode("senha123");

        // [matricula, nome, perfil, status]
        Object[][] users = {
                { 20260101, "João Silva", PerfilUsuario.MEMBER, StatusUsuario.ATIVO },
                { 20260102, "Maria Santos", PerfilUsuario.MEMBER, StatusUsuario.ATIVO },
                { 20260103, "Pedro Fernandes", PerfilUsuario.MEMBER, StatusUsuario.ATIVO },
                { 20260104, "Ana Costa", PerfilUsuario.MEMBER, StatusUsuario.ATIVO },
                { 20260105, "Carlos Neto", PerfilUsuario.MEMBER, StatusUsuario.ATIVO },
                { 20260106, "Luísa Mendes", PerfilUsuario.MEMBER, StatusUsuario.ATIVO },
                { 20260107, "Ricardo Tavares", PerfilUsuario.MEMBER, StatusUsuario.BLOQUEADO },
                { 20260108, "Sofia Bento", PerfilUsuario.MEMBER, StatusUsuario.ATIVO }
        };

        for (Object[] u : users) {
            int matricula = (Integer) u[0];
            String email = matricula + "@isptec.co.ao";
            Usuario user = Usuario.builder()
                    .matricula(matricula)
                    .nome((String) u[1])
                    .email(email)
                    .senha(encodedPassword)
                    .perfil((PerfilUsuario) u[2])
                    .status((StatusUsuario) u[3])
                    .build();
            user = usuarioRepository.save(user);
            map.put(email, user);
            log.info("  👤 Criado: {} ({})", u[1], email);
        }
        return map;
    }

    // ========================= EMPRÉSTIMOS =========================
    private void criarEmprestimos(Map<String, Usuario> users, Map<String, Livro> livros) {
        LocalDate hoje = LocalDate.now();

        Usuario joao = users.get("20260101@isptec.co.ao");
        Usuario maria = users.get("20260102@isptec.co.ao");
        Usuario pedro = users.get("20260103@isptec.co.ao");
        Usuario ana = users.get("20260104@isptec.co.ao");
        Usuario carlos = users.get("20260105@isptec.co.ao");
        Usuario luisa = users.get("20260106@isptec.co.ao");
        Usuario sofia = users.get("20260108@isptec.co.ao");

        Livro cleanCode = livros.get("978-0132350884");
        Livro cleanArch = livros.get("978-0134494166");
        Livro refactoring = livros.get("978-0134757599");
        Livro redes = livros.get("978-8582604274");
        Livro algo = livros.get("978-0262046305");
        Livro java = livros.get("978-0134685991");
        Livro patterns = livros.get("978-0201633610");
        Livro so = livros.get("978-8582606162");
        Livro shell = livros.get("978-8575221525");
        Livro mayombe = livros.get("978-9722121149");
        Livro things = livros.get("978-0385474542");
        Livro python = livros.get("978-1098104030");
        Livro pragmatic = livros.get("978-0135957059");
        Livro utopia = livros.get("978-9722105309");

        // --- ATRASADOS (multas!) ---
        salvarEmprestimo(joao, cleanCode, 0, StatusEmprestimo.ATRASADO, 1300.0,
                hoje.minusDays(20), hoje.minusDays(13), null);

        salvarEmprestimo(carlos, redes, 0, StatusEmprestimo.ATRASADO, 800.0,
                hoje.minusDays(15), hoje.minusDays(8), null);

        // --- ATIVOS (dentro do prazo) ---
        salvarEmprestimo(maria, java, 0, StatusEmprestimo.ATIVO, 0.0,
                hoje.minusDays(3), hoje.plusDays(4), null);

        salvarEmprestimo(ana, so, 0, StatusEmprestimo.ATIVO, 0.0,
                hoje.minusDays(2), hoje.plusDays(5), null);

        salvarEmprestimo(pedro, algo, 1, StatusEmprestimo.ATIVO, 0.0,
                hoje.minusDays(10), hoje.plusDays(4), null);

        // Sofia com 2 renovações (NÃO pode renovar mais!)
        salvarEmprestimo(sofia, python, 2, StatusEmprestimo.ATIVO, 0.0,
                hoje.minusDays(18), hoje.plusDays(3), null);

        salvarEmprestimo(luisa, pragmatic, 0, StatusEmprestimo.ATIVO, 0.0,
                hoje, hoje.plusDays(7), null);

        salvarEmprestimo(luisa, so, 0, StatusEmprestimo.ATIVO, 0.0,
                hoje.minusDays(5), hoje.plusDays(2), null);

        // --- DEVOLVIDOS (histórico para IA e relatórios) ---
        salvarEmprestimo(joao, java, 0, StatusEmprestimo.DEVOLVIDO, 0.0,
                hoje.minusDays(30), hoje.minusDays(23), hoje.minusDays(24));

        salvarEmprestimo(joao, patterns, 0, StatusEmprestimo.DEVOLVIDO, 0.0,
                hoje.minusDays(45), hoje.minusDays(38), hoje.minusDays(40));

        salvarEmprestimo(maria, cleanCode, 1, StatusEmprestimo.DEVOLVIDO, 0.0,
                hoje.minusDays(25), hoje.minusDays(11), hoje.minusDays(12));

        salvarEmprestimo(pedro, refactoring, 0, StatusEmprestimo.DEVOLVIDO, 0.0,
                hoje.minusDays(20), hoje.minusDays(13), hoje.minusDays(15));

        salvarEmprestimo(ana, mayombe, 0, StatusEmprestimo.DEVOLVIDO, 0.0,
                hoje.minusDays(35), hoje.minusDays(28), hoje.minusDays(29));

        // Ana leu 2 de Pepetela → IA deve recomendar "Predadores"
        salvarEmprestimo(ana, utopia, 0, StatusEmprestimo.DEVOLVIDO, 0.0,
                hoje.minusDays(20), hoje.minusDays(13), hoje.minusDays(14));

        salvarEmprestimo(sofia, cleanArch, 0, StatusEmprestimo.DEVOLVIDO, 0.0,
                hoje.minusDays(15), hoje.minusDays(8), hoje.minusDays(9));

        // Carlos pagou multa anterior
        salvarEmprestimo(carlos, shell, 0, StatusEmprestimo.DEVOLVIDO, 500.0,
                hoje.minusDays(25), hoje.minusDays(18), hoje.minusDays(13));

        salvarEmprestimo(luisa, things, 0, StatusEmprestimo.DEVOLVIDO, 0.0,
                hoje.minusDays(40), hoje.minusDays(33), hoje.minusDays(35));

        // PENDENTE (aguardando aprovação)
        salvarEmprestimo(sofia, refactoring, 0, StatusEmprestimo.PENDENTE, 0.0,
                hoje, hoje.plusDays(7), null);

        log.info("  📖 Empréstimos criados com sucesso");
    }

    private void salvarEmprestimo(Usuario user, Livro livro, int renovacoes,
            StatusEmprestimo status, double multa,
            LocalDate dataEmprestimo, LocalDate dataPrevista,
            LocalDate dataDevolucao) {
        Emprestimo e = Emprestimo.builder()
                .usuario(user)
                .livro(livro)
                .qtdRenovacoes(renovacoes)
                .status(status)
                .valorMulta(multa)
                .dataEmprestimo(dataEmprestimo)
                .dataPrevista(dataPrevista)
                .dataDevolucaoReal(dataDevolucao)
                .build();
        emprestimoRepository.save(e);
    }

    // ========================= RESERVAS =========================
    private void criarReservas(Map<String, Usuario> users, Map<String, Livro> livros) {
        LocalDate hoje = LocalDate.now();

        Usuario maria = users.get("20260102@isptec.co.ao");
        Usuario pedro = users.get("20260103@isptec.co.ao");
        Usuario joao = users.get("20260101@isptec.co.ao");
        Usuario sofia = users.get("20260108@isptec.co.ao");
        Usuario ana = users.get("20260104@isptec.co.ao");

        Livro redes = livros.get("978-8582604274");
        Livro so = livros.get("978-8582606162");
        Livro algo = livros.get("978-0262046305");
        Livro mayombe = livros.get("978-9722121149");

        // Redes esgotado: 2 pessoas na fila
        salvarReserva(maria, redes, 1, StatusReserva.ATIVA, hoje.minusDays(5));
        salvarReserva(pedro, redes, 2, StatusReserva.ATIVA, hoje.minusDays(3));

        // SO esgotado: João na fila
        salvarReserva(joao, so, 1, StatusReserva.ATIVA, hoje.minusDays(2));

        // Algorithms: Sofia reservou
        salvarReserva(sofia, algo, 1, StatusReserva.ATIVA, hoje);

        // Reserva concluída (histórico)
        salvarReserva(ana, mayombe, 1, StatusReserva.CONCLUIDA, hoje.minusDays(40));

        log.info("  🔖 Reservas criadas com sucesso");
    }

    private void salvarReserva(Usuario user, Livro livro, int posicao,
            StatusReserva status, LocalDate data) {
        Reserva r = Reserva.builder()
                .usuario(user)
                .livro(livro)
                .posicaoFila(posicao)
                .status(status)
                .dataReserva(data)
                .build();
        reservaRepository.save(r);
    }
}
