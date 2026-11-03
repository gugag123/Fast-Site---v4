package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** JSON enviado pelo ADMIN para aceitar ou recusar (PATCH /api/agendamentos/{id}/status). */
public record DecisaoRequest(

        @NotBlank(message = "Status é obrigatório")
        @Pattern(regexp = "ACEITO|RECUSADO", message = "Status inválido")
        String status
) {
}
