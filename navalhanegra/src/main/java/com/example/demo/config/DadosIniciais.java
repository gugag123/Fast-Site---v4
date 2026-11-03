package com.example.demo.config;

import com.example.demo.repository.ServicoRepository;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Roda sozinho quando o servidor liga:
 *  1) adiciona colunas novas em tabelas que já existiam (perfil, email)
 *  2) garante que o email de app.admin.email seja ADMIN (senha em BCrypt)
 */
@Component
public class DadosIniciais implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final JdbcTemplate jdbc;

    @Value("${app.admin.email}")
    private String email;

    @Value("${app.admin.senha}")
    private String senha;

    private final ServicoRepository servicoRepository;

    public DadosIniciais(UsuarioService usuarioService, JdbcTemplate jdbc, ServicoRepository servicoRepository) {
        this.usuarioService = usuarioService;
        this.jdbc = jdbc;
        this.servicoRepository = servicoRepository;
    }

    @Override
    public void run(String... args) {
        adicionarColunaSeFaltar("usuarios", "perfil", "VARCHAR(20) NOT NULL DEFAULT 'CLIENTE'");
        adicionarColunaSeFaltar("cadastros", "email", "VARCHAR(150) NULL");

        // Status antigos (AGENDADO/CONCLUIDO/CANCELADO) viram os novos (ACEITO/RECUSADO)
        jdbc.update("UPDATE agendamentos SET status = 'ACEITO' WHERE status IN ('AGENDADO', 'CONCLUIDO')");
        jdbc.update("UPDATE agendamentos SET status = 'RECUSADO' WHERE status = 'CANCELADO'");

        usuarioService.garantirAdmin(email, senha);
        System.out.println(">>> Administrador: " + email);

        // Se a tabela de serviços está vazia, cria alguns para a tela já abrir com dados
        if (servicoRepository.count() == 0) {
            jdbc.update("INSERT INTO servicos (nome, descricao, preco, duracao_min) VALUES "
                    + "('Corte', 'Corte masculino na tesoura ou máquina', 40.00, 30), "
                    + "('Barba', 'Barba com toalha quente e navalha', 30.00, 20), "
                    + "('Corte + Barba', 'Combo completo', 65.00, 50)");
            System.out.println(">>> Serviços iniciais criados");
        }
    }

    // Se a tabela foi criada por uma versão antiga do projeto, cria a coluna que falta
    private void adicionarColunaSeFaltar(String tabela, String coluna, String tipo) {
        Integer existe = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.COLUMNS "
                        + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                Integer.class, tabela, coluna);
        if (existe == null || existe == 0) {
            jdbc.execute("ALTER TABLE " + tabela + " ADD COLUMN " + coluna + " " + tipo);
            System.out.println(">>> Coluna criada: " + tabela + "." + coluna);
        }
    }
}
