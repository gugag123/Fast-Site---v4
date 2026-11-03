package com.example.demo.service;

import com.example.demo.dto.ServicoRequest;
import com.example.demo.model.Servico;
import com.example.demo.repository.ServicoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * SERVICE: regras de negócio dos serviços da barbearia.
 */
@Service
public class ServicoService {

    private final ServicoRepository repository;

    public ServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    public List<Servico> listar() {
        return repository.findAll();
    }

    public Servico buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Serviço #" + id + " não encontrado"));
    }

    public Servico criar(ServicoRequest dados) {
        return repository.insert(montar(dados));
    }

    public Servico atualizar(Long id, ServicoRequest dados) {
        Servico s = montar(dados);
        if (repository.update(id, s) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço #" + id + " não encontrado");
        }
        return buscarPorId(id);
    }

    public void excluir(Long id) {
        buscarPorId(id); // 404 se não existir
        // Se houver agendamentos com este serviço, o banco recusa (FK) e o ErroHandler devolve 409
        repository.delete(id);
    }

    private Servico montar(ServicoRequest dados) {
        Servico s = new Servico();
        s.setNome(dados.nome().trim());
        s.setDescricao(dados.descricao() == null ? "" : dados.descricao().trim());
        s.setPreco(dados.preco());
        s.setDuracaoMin(dados.duracaoMin());
        return s;
    }
}
