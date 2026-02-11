package com.example.bibliotecaapi.service;

import com.example.bibliotecaapi.model.Emprestimo;
import com.example.bibliotecaapi.model.Livro;
import com.example.bibliotecaapi.model.StatusEmprestimo;
import com.example.bibliotecaapi.model.StatusUsuario;
import com.example.bibliotecaapi.model.Usuario;
import com.example.bibliotecaapi.repository.EmprestimoRepository;
import com.example.bibliotecaapi.repository.LivroRepository;
import com.example.bibliotecaapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmprestimoRepository emprestimoRepository;

    public Map<String, Object> getDashboardData() {
        List<Livro> livros = livroRepository.findAll();
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<Emprestimo> emprestimos = emprestimoRepository.findAll();

        long livrosDisponiveis = livros.stream().filter(l -> l.getQtdDisponivel() > 0).count();
        long emprestimosAtivos = emprestimos.stream().filter(e -> e.getStatus() == StatusEmprestimo.ATIVO).count();
        long emprestimosAtrasados = emprestimos.stream().filter(e -> e.getStatus() == StatusEmprestimo.ATRASADO)
                .count();
        long membrosAtivos = usuarios.stream().filter(u -> u.getStatus() == StatusUsuario.ATIVO).count();
        long membrosBloqueados = usuarios.stream().filter(u -> u.getStatus() == StatusUsuario.BLOQUEADO).count();

        Double multasPendentes = emprestimos.stream()
                .filter(e -> e.getStatus() == StatusEmprestimo.ATRASADO && e.getValorMulta() > 0)
                .mapToDouble(Emprestimo::getValorMulta)
                .sum();

        // Categorias Populares (Top 5)
        Map<String, Long> categoriasCount = livros.stream()
                .collect(Collectors.groupingBy(l -> l.getCategoria().getNome(), Collectors.counting()));

        List<Map<String, Object>> categoriasPopulares = categoriasCount.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("nome", e.getKey());
                    map.put("total", e.getValue());
                    map.put("pct", (e.getValue() * 100.0) / livros.size());
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Long> livrosEmprestadosCount = emprestimos.stream()
                .collect(Collectors.groupingBy(e -> e.getLivro().getTitulo(), Collectors.counting()));

        List<Map<String, Object>> livrosMaisEmprestados = livrosEmprestadosCount.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("titulo", e.getKey());
                    map.put("emprestimos", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("emprestimosTotal", emprestimos.size());
        data.put("emprestimosAtivos", emprestimosAtivos);
        data.put("emprestimosAtrasados", emprestimosAtrasados);
        data.put("devolucoesMes",
                emprestimos.stream().filter(e -> e.getStatus() == StatusEmprestimo.DEVOLVIDO).count());
        data.put("livrosTotal", livros.size());
        data.put("livrosDisponiveis", livrosDisponiveis);
        data.put("livrosEmprestados", livros.size() - livrosDisponiveis);
        data.put("membrosTotal", usuarios.size());
        data.put("membrosAtivos", membrosAtivos);
        data.put("membrosBloqueados", membrosBloqueados);
        data.put("multasTotalPendentes", multasPendentes);
        data.put("multasTotalPagas", 0);
        data.put("emprestimosPoMes", List.of());
        data.put("categoriasMaisPopulares", categoriasPopulares);
        data.put("livrosMaisEmprestados", livrosMaisEmprestados);

        return data;
    }
}
