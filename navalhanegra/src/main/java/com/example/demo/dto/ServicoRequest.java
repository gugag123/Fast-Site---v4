package com.example.demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * JSON enviado pelo formulário de serviços (POST e PUT /api/servicos).
 */
public record ServicoRequest(

        @NotBlank(message = "Nome do serviço é obrigatório")
        @Size(max = 100, message = "Nome muito longo")
        String nome,

        @Size(max = 255, message = "Descrição muito longa")
        String descricao,

        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
        BigDecimal preco,

        @NotNull(message = "Duração é obrigatória")
        @Min(value = 5, message = "Duração mínima: 5 minutos")
        @Max(value = 480, message = "Duração máxima: 480 minutos")
        Integer duracaoMin
) {
}
