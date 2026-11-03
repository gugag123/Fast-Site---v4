package com.example.demo.repository;

import com.example.demo.model.Agendamento;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY: tabela "agendamentos" (Spring JDBC).
 * O JOIN traz o nome do cliente (cadastros) e do serviço (servicos).
 */
@Repository
public class AgendamentoRepository {

    private static final String SELECT =
            "SELECT a.id, a.cadastro_id, a.servico_id, a.data, a.horario, a.valor, a.status, "
                    + "c.nome AS cliente_nome, s.nome AS servico_nome "
                    + "FROM agendamentos a "
                    + "JOIN cadastros c ON c.id = a.cadastro_id "
                    + "JOIN servicos s ON s.id = a.servico_id ";

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final JdbcTemplate jdbc;

    public AgendamentoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Agendamento> mapper = (rs, linha) -> {
        Agendamento a = new Agendamento();
        a.setId(rs.getLong("id"));
        a.setCadastroId(rs.getLong("cadastro_id"));
        a.setClienteNome(rs.getString("cliente_nome"));
        a.setServicoId(rs.getLong("servico_id"));
        a.setServicoNome(rs.getString("servico_nome"));
        a.setData(rs.getDate("data").toLocalDate().toString());
        a.setHorario(rs.getTime("horario").toLocalTime().format(HORA));
        a.setValor(rs.getBigDecimal("valor"));
        a.setStatus(rs.getString("status"));
        return a;
    };

    public List<Agendamento> findAll() {
        return jdbc.query(SELECT + "ORDER BY a.data DESC, a.horario DESC", mapper);
    }

    public Optional<Agendamento> findById(Long id) {
        return jdbc.query(SELECT + "WHERE a.id = ?", mapper, id).stream().findFirst();
    }

    // Agendamentos de um cliente (tela "Meus agendamentos")
    public List<Agendamento> findByCadastroId(Long cadastroId) {
        return jdbc.query(SELECT + "WHERE a.cadastro_id = ? ORDER BY a.data DESC, a.horario DESC", mapper, cadastroId);
    }

    // Já existe outro agendamento ACEITO nesse dia e horário? (pendentes não bloqueiam)
    public boolean existeHorarioOcupado(LocalDate data, LocalTime horario, Long ignorarId) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM agendamentos WHERE data = ? AND horario = ? AND status = 'ACEITO' AND id <> ?",
                Integer.class, Date.valueOf(data), Time.valueOf(horario), ignorarId);
        return total != null && total > 0;
    }

    public long insert(Agendamento a, LocalDate data, LocalTime horario) {
        String sql = "INSERT INTO agendamentos (cadastro_id, servico_id, data, horario, valor, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder chave = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, a.getCadastroId());
            ps.setLong(2, a.getServicoId());
            ps.setDate(3, Date.valueOf(data));
            ps.setTime(4, Time.valueOf(horario));
            ps.setBigDecimal(5, a.getValor());
            ps.setString(6, a.getStatus());
            return ps;
        }, chave);
        return chave.getKey().longValue();
    }

    // O admin só muda o status (ACEITO ou RECUSADO)
    public int atualizarStatus(Long id, String status) {
        return jdbc.update("UPDATE agendamentos SET status = ? WHERE id = ?", status, id);
    }
}
