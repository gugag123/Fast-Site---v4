package com.example.demo.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Transforma os erros em JSON simples { "mensagem": "..." } para a tela exibir.
 */
@RestControllerAdvice
public class ErroHandler {

    // Campo obrigatório vazio ou inválido -> 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacao(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(Map.of("mensagem", msg));
    }

    // JSON mal formado -> 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> jsonInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(Map.of("mensagem", "Dados enviados em formato inválido"));
    }

    // Email repetido no banco -> 409
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, String>> duplicado(DuplicateKeyException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensagem", "Este email já possui cadastro"));
    }

    // Violação de chave estrangeira (ex.: excluir serviço que já tem agendamentos) -> 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> integridade(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensagem",
                "Não é possível concluir: este registro está ligado a outros (ex.: serviço com agendamentos)"));
    }

    // Rota /api/... que não existe -> 404 em JSON
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> rotaInexistente(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensagem", "Recurso não encontrado"));
    }

    // Ex.: cadastro não encontrado (404), email já cadastrado (409)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> status(ResponseStatusException e) {
        String msg = e.getReason() != null ? e.getReason() : "Erro";
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("mensagem", msg));
    }

    // Qualquer outro erro: mostra no console E manda o motivo para a tela (ajuda a descobrir o problema)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> outro(Exception e) {
        e.printStackTrace();
        Throwable causa = e;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensagem", "Erro no servidor: " + causa.getClass().getSimpleName()
                        + " - " + causa.getMessage()));
    }
}
