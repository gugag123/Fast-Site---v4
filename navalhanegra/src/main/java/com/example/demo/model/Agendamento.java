package com.example.demo.model;

import java.math.BigDecimal;

/**
 * MODEL 4: agendamento de um cliente para um serviço (tabela "agendamentos").
 * O cliente solicita o horário e o administrador aceita ou recusa.
 * Tem duas chaves estrangeiras: cadastro_id (cliente) e servico_id (serviço).
 * clienteNome e servicoNome vêm do JOIN, só para mostrar na listagem.
 * Data e horário ficam como texto (yyyy-MM-dd e HH:mm) para o JSON não dar erro.
 */
public class Agendamento {

    // O cliente solicita (PENDENTE) e o admin só decide: ACEITO ou RECUSADO
    public static final String PENDENTE = "PENDENTE";
    public static final String ACEITO = "ACEITO";
    public static final String RECUSADO = "RECUSADO";

    private Long id;
    private Long cadastroId;
    private String clienteNome;
    private Long servicoId;
    private String servicoNome;
    private String data;
    private String horario;
    private BigDecimal valor;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCadastroId() { return cadastroId; }
    public void setCadastroId(Long cadastroId) { this.cadastroId = cadastroId; }

    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }

    public Long getServicoId() { return servicoId; }
    public void setServicoId(Long servicoId) { this.servicoId = servicoId; }

    public String getServicoNome() { return servicoNome; }
    public void setServicoNome(String servicoNome) { this.servicoNome = servicoNome; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
