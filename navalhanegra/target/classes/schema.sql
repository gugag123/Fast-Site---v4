-- Executado automaticamente pelo Spring Boot quando o servidor liga.
-- IF NOT EXISTS: se a tabela já existe, não mexe nela.
-- (colunas novas em tabelas antigas são criadas pela classe DadosIniciais)

-- Contas de login. perfil: ADMIN (vê os cadastros) ou CLIENTE
CREATE TABLE IF NOT EXISTS usuarios (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    perfil     VARCHAR(20)  NOT NULL DEFAULT 'CLIENTE'
) ENGINE=InnoDB;

-- Dados dos clientes (o email liga o cadastro ao login em "usuarios")
CREATE TABLE IF NOT EXISTS cadastros (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(150) NOT NULL,
    email        VARCHAR(150) NULL,
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
