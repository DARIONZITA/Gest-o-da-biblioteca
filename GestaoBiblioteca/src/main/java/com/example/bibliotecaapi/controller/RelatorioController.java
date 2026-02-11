package com.example.bibliotecaapi.controller;

import com.example.bibliotecaapi.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "Endpoints para relatórios e dashboards")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obter dados do dashboard", description = "Retorna estatísticas gerais do sistema")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        return ResponseEntity.ok(relatorioService.getDashboardData());
    }
}
