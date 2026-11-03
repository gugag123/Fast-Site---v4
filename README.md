# NAVALHA NEGRA — Projeto Integrador Parte 4
Barbearia · clientes e agendamentos · Aluno: Guilherme Felberque

## Como rodar
1. Ligue o MySQL. Se a senha do seu `root` não for vazia, troque `spring.datasource.password` em `src/main/resources/application.properties`.
2. No mesmo arquivo, coloque **o seu gmail** em `app.admin.email` (e uma senha em `app.admin.senha`). Esse é o único email que vê os cadastros salvos.
3. Rode `DemoApplication`.
   O Spring cria sozinho o banco, as tabelas (`schema.sql`) e a conta do administrador. Se as tabelas já existiam, as colunas novas (`perfil`, `email`) são adicionadas sozinhas.
   Usa o driver mysql-connector-java 5.1.49, compatível com MySQL antigo.
4. Abra **http://localhost:8080**

## Quem pode fazer o quê
| Pessoa | Pode |
|---|---|
| Qualquer visitante | Criar cadastro (tela "Cadastre-se"), que já cria o login dele |
| Cliente logado | Ver a tela de boas-vindas |
| Administrador (o gmail do `application.properties`) | Ver a tela de boas-vindas **e** os cadastros salvos (lista, ficha, excluir) |

A proteção está no Java (`AdminInterceptor`), não só na tela: um cliente que tentar chamar `/api/cadastros` recebe 403.

## Estrutura (arquitetura em camadas)
```
src/main/java/com/example/demo
├── DemoApplication.java
├── model/        Usuario, Cadastro, Servico, Agendamento          (classes de dados)
├── repository/   Usuario/Cadastro/Servico/AgendamentoRepository  (Spring JDBC / JdbcTemplate)
├── service/      Usuario/Cadastro/Servico/AgendamentoService     (regras de negócio)
├── controller/   AuthController, CadastroController, ServicoController, AgendamentoController, ErroHandler
├── dto/          LoginRequest, CadastroRequest, ServicoRequest, AgendamentoRequest
├── config/       DadosIniciais (admin + colunas novas), AdminInterceptor, WebConfig
└── util/         SenhaUtil (BCrypt)
src/main/resources/static
├── index.html       Tela 1: Login
├── cadastro.html    Cadastro público (6 campos + email e senha)
├── home.html        Boas-vindas com imagem em tela cheia
├── cadastros.html   Cadastros salvos (só admin)
├── servicos.html    CLIENTE escolhe o serviço, data e horário e vê "Meus agendamentos"
├── gerenciar-servicos.html  ADMIN: CRUD do catálogo de serviços
├── agendamentos.html ADMIN: só ACEITA ou RECUSA os pedidos dos clientes
├── css/style.css    Identidade visual (preto/amarelo, flat, alto contraste)
├── js/sessao.js, js/crud.js   (crud.js: toast, modal de confirmação, exportar CSV)
└── img/hero.svg     Imagem fullscreen
```

## Endpoints
| Método | Rota | Quem | O que faz |
|---|---|---|---|
| POST | `/api/login` | todos | Login (JSON `{email, senha}`) |
| GET | `/api/usuario` | logado | Quem está logado e se é admin |
| POST | `/api/logout` | logado | Sai |
| POST | `/api/cadastros` | todos | Cria cadastro + login → "Cadastro realizado com sucesso!" |
| GET | `/api/cadastros` | admin | Lista todos |
| GET | `/api/cadastros/{id}` | admin | Busca um |
| DELETE | `/api/cadastros/{id}` | admin | Exclui (soft delete) e remove o login do cliente |
| GET | `/api/servicos` | logado | Lista serviços (cliente escolhe daqui) |
| GET | `/api/servicos/{id}` | logado | Um serviço (200 ou 404) |
| POST | `/api/servicos` | admin | Cria (201) |
| PUT | `/api/servicos/{id}` | admin | Atualiza (200) |
| DELETE | `/api/servicos/{id}` | admin | Exclui (204); 409 se houver agendamentos com ele |
| POST | `/api/agendamentos` | cliente | Solicita horário (201, status PENDENTE); 409 se o horário já foi aceito para outro |
| GET | `/api/agendamentos/meus` | cliente | Lista os agendamentos do cliente logado |
| GET | `/api/agendamentos` | admin | Lista todos (com nome do cliente e do serviço) |
| GET | `/api/agendamentos/{id}` | admin | Um agendamento (200 ou 404) |
| PATCH | `/api/agendamentos/{id}/status` | admin | Aceita ou recusa (`{"status":"ACEITO"}` ou `"RECUSADO"`) |

## Fluxo do agendamento
1. Cliente entra em **Serviços**, clica no serviço que quer, escolhe data e horário → pedido fica **Pendente**.
2. Admin entra em **Agendamentos** e clica em **Aceitar** ou **Recusar**. Só isso.
3. O cliente vê o resultado em "Meus agendamentos". Dois pedidos não podem ser aceitos no mesmo dia e horário.

## Roteiro para apresentar ao professor
1. **Senha incorreta**: na tela de login, digite uma senha errada e mostre a caixa vermelha "Email ou senha inválidos".
2. **Inserção**: clique em "Cadastre-se" e cadastre 3 clientes completos, cada um com um email diferente.
3. **Feedback**: depois de cada um aparece "Cadastro realizado com sucesso!".
4. **Transição de sessão**: entre com o login de um cliente; abre a tela com a imagem em tela cheia, sem acesso aos cadastros.
5. **Recuperação individual**: saia e entre com o gmail de administrador → "Ver cadastros salvos" → "Ver" em cada cliente.
6. **Auditoria do banco**: no Workbench rode `SELECT email, perfil, senha_hash FROM usuarios;` e mostre que as senhas estão como `$2a$10$...`.

## Desafios extras feitos
- ⭐ 1: máscaras de CEP `00000-000` e telefone `(00) 90000-0000`
- ⭐ 2: ViaCEP preenche o endereço ao digitar o CEP (precisa de internet)
- ⭐ 3: campo `data_criacao` automático + botão Excluir (soft delete, campo `ativo`)

## Parte 4: o que foi feito
- **API pública**: ViaCEP no cadastro (digitou o CEP, preenche o endereço).
- **Tela 1 – Serviços** (`servicos.html`): nome, descrição, preço, duração.
- **Tela 2 – Agendamentos** (`agendamentos.html`): cliente, serviço, data, horário, valor, status.
- Cada tela tem Create, Read (tabela + busca), Update (botão Editar carrega o formulário) e Delete (modal de confirmação).
- Avisos: "Registro criado com sucesso", "Registro atualizado", "Registro excluído".
- Menu lateral com todas as telas (e botões no `home.html`).
- Tabelas novas em `schema.sql`/`banco.sql`; 3 serviços iniciais são criados se a tabela estiver vazia.
- ⭐ Desafio 1: `agendamentos` tem chaves estrangeiras (cliente e serviço), por SQL/JDBC.
- ⭐ Desafio 2: botão "Exportar Excel (CSV)" nas duas listagens (respeita o filtro da busca).
- ⭐ Desafio 3: `ErroHandler` (@RestControllerAdvice) devolve JSON amigável para 400, 404, 409 e 500.

## Roteiro da Parte 4
1. Cadastre-se com um CEP e mostre o endereço sendo preenchido (ViaCEP).
2. Entre como admin → menu: Serviços e Agendamentos.
3. Serviços: cadastrar, buscar, **Editar**, **Excluir** (modal).
4. Agendamentos: criar (escolhe cliente e serviço), editar (ex.: mudar status), excluir.
5. No Workbench: `SELECT * FROM servicos;` e `SELECT * FROM agendamentos;` mostram as mudanças.
6. Tente excluir um serviço que já tem agendamento → mensagem de erro amigável (409).
