package com.example.demo.controller;

import com.example.demo.dto.AgendamentoRequest;
import com.example.demo.dto.DecisaoRequest;
import com.example.demo.model.Agendamento;
import com.example.demo.service.AgendamentoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CONTROLLER: agendamentos (permissões no AdminInterceptor).
 *   POST  /api/agendamentos              -> CLIENTE solicita (201, fica PENDENTE)
 *   GET   /api/agendamentos/meus         -> CLIENTE vê os dele
 *   GET   /api/agendamentos              -> ADMIN lista todos
 *   GET   /api/agendamentos/{id}         -> ADMIN vê um (200 ou 404)
 *   PATCH /api/agendamentos/{id}/status  -> ADMIN aceita ou recusa
 */
@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Agendamento> listar() {
        return service.listar();
    }

    @GetMapping("/meus")
    public List<Agendamento> meus(HttpSession session) {
        return service.listarDoCliente(emailLogado(session));
    }

    @GetMapping("/{id}")
    public Agendamento buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Agendamento> solicitar(@Valid @RequestBody AgendamentoRequest dados, HttpSession session) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.solicitar(emailLogado(session), dados));
    }

    @PatchMapping("/{id}/status")
    public Agendamento decidir(@PathVariable Long id, @Valid @RequestBody DecisaoRequest dados) {
        return service.decidir(id, dados.status());
    }

    private String emailLogado(HttpSession session) {
        return String.valueOf(session.getAttribute(AuthController.SESSAO_EMAIL));
    }
}
