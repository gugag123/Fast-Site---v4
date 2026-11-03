package com.example.demo.repository;

import com.example.demo.model.Servico;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY: tabela "servicos" (Spring JDBC).
 */
@Repository
public class ServicoRepository {

    private final JdbcTemplate jdbc;

    public ServicoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Servico> mapper = (rs, linha) -> {
        Servico s = new Servico();
        s.setId(rs.getLong("id"));
        s.setNome(rs.getString("nome"));
        s.setDescricao(rs.getString("descricao"));
        s.setPreco(rs.getBigDecimal("preco"));
        s.setDuracaoMin(rs.getInt("duracao_min"));
        return s;
    };

    public List<Servico> findAll() {
        return jdbc.query("SELECT * FROM servicos ORDER BY nome", mapper);
    }

    public Optional<Servico> findById(Long id) {
        return jdbc.query("SELECT * FROM servicos WHERE id = ?", mapper, id).stream().findFirst();
    }

    public long count() {
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM servicos", Long.class);
        return total == null ? 0 : total;
    }

    public Servico insert(Servico s) {
        String sql = "INSERT INTO servicos (nome, descricao, preco, duracao_min) VALUES (?, ?, ?, ?)";
        KeyHolder chave = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, s.getNome());
            ps.setString(2, s.getDescricao());
            ps.setBigDecimal(3, s.getPreco());
            ps.setInt(4, s.getDuracaoMin());
            return ps;
        }, chave);
        s.setId(chave.getKey().longValue());
        return s;
    }

    // Devolve quantas linhas foram alteradas (0 = id não existe)
    public int update(Long id, Servico s) {
        return jdbc.update("UPDATE servicos SET nome = ?, descricao = ?, preco = ?, duracao_min = ? WHERE id = ?",
                s.getNome(), s.getDescricao(), s.getPreco(), s.getDuracaoMin(), id);
    }

    public int delete(Long id) {
        return jdbc.update("DELETE FROM servicos WHERE id = ?", id);
    }
}
