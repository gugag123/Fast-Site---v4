-- ============================================================
-- NAVALHA NEGRA - banco de dados (MySQL)
--
-- NÃO é obrigatório rodar este script: quando o servidor liga, o Spring
-- cria o banco barbearia_db e executa o schema.sql (CREATE TABLE IF NOT EXISTS),
-- e o usuário admin é criado automaticamente com a senha em BCrypt.
-- Se preferir criar tudo à mão no Workbench, rode este arquivo.
-- ============================================================
CREATE DATABASE IF NOT EXISTS barbearia_db;
USE barbearia_db;

-- Entidade 1: usuários do login (senha SEMPRE em hash BCrypt)
CREATE TABLE IF NOT EXISTS usuarios (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    perfil     VARCHAR(20)  NOT NULL DEFAULT 'CLIENTE'   -- ADMIN ou CLIENTE
) ENGINE=InnoDB;

-- Entidade 2: cadastros de clientes (6 campos obrigatórios + extras do desafio 3)
CREATE TABLE IF NOT EXISTS cadastros (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(150) NOT NULL,
    email        VARCHAR(150) NULL,                      -- liga o cadastro ao login
    endereco     VARCHAR(200) NOT NULL,
    cep          VARCHAR(9)   NOT NULL,
    numero       VARCHAR(10)  NOT NULL,
    telefone     VARCHAR(20)  NOT NULL,
    observacao   TEXT,
    data_criacao DATETIME     NOT NULL,
    ativo        TINYINT(1)   NOT NULL DEFAULT 1
) ENGINE=InnoDB;

-- Serviços oferecidos (Tela 1 da Parte 4)
CREATE TABLE IF NOT EXISTS servicos (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(100)  NOT NULL,
    descricao   VARCHAR(255),
    preco       DECIMAL(10,2) NOT NULL,
    duracao_min INT           NOT NULL
) ENGINE=InnoDB;

-- Agendamentos (Tela 2 da Parte 4): chave estrangeira para o cliente e para o serviço
CREATE TABLE IF NOT EXISTS agendamentos (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    cadastro_id INT           NOT NULL,
    servico_id  INT           NOT NULL,
    data        DATE          NOT NULL,
    horario     TIME          NOT NULL,
    valor       DECIMAL(10,2) NOT NULL,
    status      VARCHAR(20)   NOT NULL DEFAULT 'PENDENTE',
    CONSTRAINT fk_agend_cliente FOREIGN KEY (cadastro_id) REFERENCES cadastros(id),
    CONSTRAINT fk_agend_servico FOREIGN KEY (servico_id)  REFERENCES servicos(id)
) ENGINE=InnoDB;

-- ============================================================
-- AUDITORIA (passo 6 do roteiro): mostre ao professor que a senha está criptografada
-- ============================================================
-- SELECT id, email, perfil, senha_hash FROM usuarios;
--   -> senha_hash aparece como $2a$10$.... (BCrypt) para TODOS (admin e clientes)
--
-- SELECT * FROM cadastros;
--
-- Ver cada cliente junto com o login dele:
-- SELECT c.id, c.nome, c.email, u.perfil, u.senha_hash
--   FROM cadastros c JOIN usuarios u ON u.email = c.email;
