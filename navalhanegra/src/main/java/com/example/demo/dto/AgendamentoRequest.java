package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * JSON enviado pelo CLIENTE ao solicitar um horário (POST /api/agendamentos).
 * O cliente é descoberto pela sessão e o valor vem do serviço escolhido.
 * data = "2026-11-20", horario = "14:30".
 */
public record AgendamentoRequest(

        @NotNull(message = "Selecione o serviço")
        Long servicoId,

        @NotBlank(message = "Data é obrigatória")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Data inválida")
        String data,

        @NotBlank(message = "Horário é obrigatório")
        @Pattern(regexp = "\\d{2}:\\d{2}", message = "Horário inválido")
        String horario
) {
}
