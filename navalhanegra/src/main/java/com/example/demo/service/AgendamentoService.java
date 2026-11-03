package com.example.demo.service;

import com.example.demo.dto.AgendamentoRequest;
import com.example.demo.model.Agendamento;
import com.example.demo.model.Cadastro;
import com.example.demo.model.Servico;
import com.example.demo.repository.AgendamentoRepository;
import com.example.demo.repository.CadastroRepository;
import com.example.demo.repository.ServicoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * SERVICE: regras de negócio dos agendamentos.
 *  - o CLIENTE escolhe serviço, data e horário -> nasce PENDENTE
 *  - o ADMIN só aceita ou recusa (e não pode aceitar dois no mesmo horário)
 */
@Service
public class AgendamentoService {

    private final AgendamentoRepository repository;
    private final CadastroRepository cadastroRepository;
    private final ServicoRepository servicoRepository;

    public AgendamentoService(AgendamentoRepository repository,
                              CadastroRepository cadastroRepository,
                              ServicoRepository servicoRepository) {
        this.repository = repository;
        this.cadastroRepository = cadastroRepository;
        this.servicoRepository = servicoRepository;
    }

    public List<Agendamento> listar() {
        return repository.findAll();
    }

    public List<Agendamento> listarDoCliente(String emailLogado) {
        return repository.findByCadastroId(cadastroDo(emailLogado).getId());
    }

    public Agendamento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Agendamento #" + id + " não encontrado"));
    }

    // CLIENTE solicita um horário: fica PENDENTE até o admin decidir
    public Agendamento solicitar(String emailLogado, AgendamentoRequest dados) {
        Cadastro cliente = cadastroDo(emailLogado);
        Servico servico = servicoRepository.findById(dados.servicoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Serviço não encontrado"));
        LocalDate data = lerData(dados.data());
        LocalTime horario = lerHorario(dados.horario());

        if (LocalDateTime.of(data, horario).isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escolha uma data e horário futuros");
        }
        if (repository.existeHorarioOcupado(data, horario, 0L)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esse horário já está ocupado, escolha outro");
        }

        Agendamento a = new Agendamento();
        a.setCadastroId(cliente.getId());
        a.setServicoId(servico.getId());
        a.setValor(servico.getPreco());
        a.setStatus(Agendamento.PENDENTE);
        long id = repository.insert(a, data, horario);
        return buscarPorId(id);
    }

    // ADMIN aceita ou recusa um agendamento que ainda está PENDENTE
    public Agendamento decidir(Long id, String novoStatus) {
        Agendamento atual = buscarPorId(id);
        if (!Agendamento.PENDENTE.equals(atual.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esse agendamento já foi respondido");
        }
        if (Agendamento.ACEITO.equals(novoStatus)
                && repository.existeHorarioOcupado(LocalDate.parse(atual.getData()),
                        LocalTime.parse(atual.getHorario()), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe um agendamento aceito nesse dia e horário");
        }
        repository.atualizarStatus(id, novoStatus);
        return buscarPorId(id);
    }

    // Descobre qual cadastro pertence ao login (o admin não tem cadastro de cliente)
    private Cadastro cadastroDo(String email) {
        return cadastroRepository.findAtivoByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Só clientes cadastrados podem solicitar agendamentos"));
    }

    private LocalDate lerData(String texto) {
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data inválida");
        }
    }

    private LocalTime lerHorario(String texto) {
        try {
            return LocalTime.parse(texto);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Horário inválido");
        }
    }
}
