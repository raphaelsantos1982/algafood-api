# Arquitetura do AlgaFood API

Documento de referência do projeto: stack, organização de pacotes, modelo de domínio, endpoints, integrações, segurança e configuração. Descreve o estado atual do código.

> Gerado a partir do código-fonte em novembro/2026. Quando o código mudar, atualize este documento.

---

## 1. Visão geral

API REST de uma plataforma de delivery de restaurantes. Permite gerenciar cozinhas, estados, cidades, restaurantes, produtos, formas de pagamento, usuários, grupos, permissões e pedidos, além de gerar relatórios e enviar e-mails transacionais.

| Item | Valor |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 2.7.12 |
| Persistência | Spring Data JPA + Hibernate 5.6 |
| Banco | MySQL 8 (`algafood`) |
| Migrações | Flyway 8.5 |
| Build | Maven |
| Pacote base | `com.algaworks.algafood` |

---

## 2. Estrutura de pacotes

O código segue uma separação em camadas, com a API independente do domínio.

```
com.algaworks.algafood
├── api/                 Camada de apresentação (REST)
│   ├── v1/              Controllers, models de entrada/saída, assemblers, OpenAPI da v1
│   ├── v2/              Controllers da v2 (hoje: cozinhas e cidades)
│   └── exceptionhandler/  Tratamento global de erros (RFC 7807 / Problem)
├── domain/              Regras de negócio
│   ├── model/           Entidades JPA (agregados)
│   ├── repository/      Interfaces de repositório
│   ├── service/         Serviços de cadastro, fluxo de pedido, relatórios, storage, e-mail
│   ├── event/           Eventos de domínio (PedidoConfirmado, PedidoCancelado)
│   ├── listener/        Ouvintes dos eventos (notificação ao cliente por e-mail)
│   ├── exception/       Exceções de negócio
│   └── filter/          Filtros de consulta (ex.: VendaDiariaFilter)
├── infrastructure/      Implementações técnicas
│   ├── repository/      Repositórios customizados, Specifications, implementação de consultas
│   ├── service/
│   │   ├── storage/     Armazenamento de fotos: local e S3 (3 buckets)
│   │   ├── email/       Envio de e-mail: fake, sandbox e SMTP
│   │   ├── report/      Relatório PDF de vendas
│   │   └── query/       Consultas de estatísticas
└── core/                Configurações transversais
    ├── security/        WebSecurityConfig (Spring Security)
    ├── web/             Config MVC, HAL, media types, deprecação de API
    ├── data/            Paginação
    ├── validation/      Validadores customizados
    ├── storage/ email/  Propriedades e configuração das integrações
    ├── jackson/ squiggly/ modelmapper/ springfox/
    └── ...
```

**Fluxo de uma requisição:** `Controller` → `Input Disassembler` (DTO → entidade) → `Service de domínio` → `Repository` → banco. Na volta, `Entidade` → `Model Assembler` → `Model` (DTO de saída, com links HAL).

---

## 3. Modelo de domínio

### 3.1 Entidades principais

| Entidade | Descrição | Relacionamentos |
|---|---|---|
| `Estado` | Unidade federativa | — |
| `Cidade` | Cidade | `@ManyToOne` → `Estado` |
| `Cozinha` | Tipo de cozinha (ex.: italiana) | `@OneToMany` ← `Restaurante` |
| `Restaurante` | Estabelecimento | `@ManyToOne` → `Cozinha`; `@ManyToMany` → `FormaPagamento`; `@ManyToMany` → `Usuario` (responsáveis); `@OneToMany` → `Produto`; Endereco embutido |
| `Produto` | Item do cardápio | `@ManyToOne` → `Restaurante`; `@OneToOne` ← `FotoProduto` |
| `FotoProduto` | Foto de um produto | `@OneToOne` → `Produto` (lazy) |
| `FormaPagamento` | Forma de pagamento (ex.: cartão) | — |
| `Pedido` | Pedido de um cliente (raiz de agregado) | `@ManyToOne` → `Restaurante`, `Usuario` (cliente), `FormaPagamento`; `@OneToMany` → `ItemPedido` (cascade ALL); Endereco embutido |
| `ItemPedido` | Item de um pedido | `@ManyToOne` → `Pedido`, `Produto` |
| `Usuario` | Usuário do sistema | `@ManyToMany` → `Grupo` |
| `Grupo` | Conjunto de permissões | `@ManyToMany` → `Permissao` |
| `Permissao` | Permissão de acesso | — |
| `Endereco` | Endereço (embutido) | `@ManyToOne` → `Cidade` (lazy) |

### 3.2 Ciclo de vida do pedido

`Pedido` é um agregado que publica eventos de domínio (`AbstractAggregateRoot`). O status é controlado por `StatusPedido`:

```
CRIADO ──confirmar──► CONFIRMADO ──entregar──► ENTREGUE
   │
   └──cancelar──► CANCELADO
```

- `confirmar` publica `PedidoConfirmadoEvent`.
- `cancelar` publica `PedidoCanceladoEvent`.
- Os listeners `NotificacaoClientePedidoConfirmadoListener` e `NotificacaoClientePedidoCanceladoListener` enviam e-mail ao cliente.
- As transições válidas são definidas em `StatusPedido` (`podeAlterarPara` / `naoPodeAlterarPara`).

---

## 4. Banco de dados

- Schema versionado com **Flyway**, em `src/main/resources/db/migration` (V001 a V013).
- `db/testdata/afterMigrate.sql` carrega dados de teste após as migrações (configurado em `spring.flyway.locations`).
- O schema é criado pelas migrações; `ddl-auto` está desativado.
- Dialeto: `MySQL57Dialect`.

---

## 5. API REST

Todas as rotas usam JSON (com suporte a HAL). A versão faz parte do caminho.

### 5.1 Versionamento

- Caminhos `/v1/...` e `/v2/...`.
- Endpoints marcados como depreciados retornam um header informando a substituição (`ApiDeprecationHandler`). Ex.: `GET /v1/cidades` está depreciado em favor de `/v2/cidades`.
- Documentação: SpringFox 3.0 + Swagger UI, com abas separadas para v1 e v2.

### 5.2 Endpoints v1

| Recurso | Caminho base | Operações |
|---|---|---|
| Cozinhas | `/v1/cozinhas` | CRUD |
| Estados | `/v1/estados` | CRUD |
| Cidades | `/v1/cidades` | CRUD (depreciado) |
| Formas de pagamento | `/v1/formas-pagamento` | CRUD |
| Restaurantes | `/v1/restaurantes` | CRUD, PATCH de campos, ativação/inativação em lote (`/ativacoes`) |
| Produtos do restaurante | `/v1/restaurantes/{restauranteId}/produtos` | listar, criar, atualizar |
| Foto do produto | `/v1/restaurantes/{restauranteId}/produtos/{produtoId}/foto` | consultar, enviar, remover |
| Formas de pagamento do restaurante | `/v1/restaurantes/{restauranteId}/formas-pagamento` | associar, desassociar |
| Responsáveis do restaurante | `/v1/restaurantes/{restauranteId}/responsaveis` | listar, associar, desassociar |
| Pedidos | `/v1/pedidos` | listar, consultar por código, criar |
| Fluxo do pedido | `/v1/pedidos/{codigoPedido}` | `confirmacao`, `cancelamento`, `entrega` (PUT) |
| Usuários | `/v1/usuarios` | CRUD, alteração de senha |
| Grupos | `/v1/grupos` | CRUD |
| Grupos do usuário | `/v1/usuarios/{usuarioId}/grupos` | associar, desassociar |
| Permissões do grupo | `/v1/grupos/{grupoId}/permissoes` | associar, desassociar |
| Permissões | `/v1/permissoes` | listar |
| Estatísticas | `/v1/estatisticas` | `vendas-diarias` (com filtros) e relatório PDF |
| Entry point | raiz da API | links HAL dos recursos |
| Chat (ChatGPT) | `/api/chat` | `POST /ask` |

### 5.3 Endpoints v2

| Recurso | Caminho base | Operações |
|---|---|---|
| Cozinhas | `/v2/cozinhas` | CRUD |
| Cidades | `/v2/cidades` | CRUD |

### 5.4 Erros

`ExceptionHandler` global (`api/exceptionhandler`) devolve respostas no formato `Problem` com tipos definidos em `ProblemType`:

- `/dados-invalidos`, `/mensagem-incompreensivel`, `/parametro-invalido` (400)
- `/recurso-nao-encontrado` (404)
- `/entidade-em-uso` (409)
- `/erro-negocio` (regras de negócio)
- `/erro-de-sistema` (500)

Rotas inexistentes geram `NoHandlerFoundException` (`spring.mvc.throw-exception-if-no-handler-found=true`), em vez de um 404 padrão do Spring.

---

## 6. Integrações externas

| Integração | Onde | Finalidade | Configuração |
|---|---|---|---|
| **Amazon S3** | `infrastructure/service/storage` | Armazenamento de fotos de produtos. Há três implementações (`S3FotoStorageService`, `S3NovoFotoStorageService`, `S3TerceiroFotoStorageService`), uma por bucket de estudo | `algafood.storage.tipo=s3`, `algafood.storage.s3*.bucket`, `regiao`, `diretorio-fotos` |
| **Armazenamento local** | `LocalFotoStorageService` | Alternativa de storage em disco | `algafood.storage.tipo=local`, `algafood.storage.local.diretorio-fotos` |
| **Amazon SES (SMTP)** | `infrastructure/service/email` | E-mails transacionais (confirmação e cancelamento de pedido, em HTML) | `spring.mail.*`, `algafood.email.impl` (`fake`, `sandbox`, `smtp`) |
| **OpenAI (ChatGPT)** | `domain/service/ChatGPTService` | Endpoint de chat `/api/chat/ask` | `openai.api.key`, `openai.api.url` |
| **Loggly** | `logback-spring.xml` | Envio de logs da aplicação | `logging.loggly.token` (variável `LOGGLY_TOKEN`), somente com o profile `production` |

> A conta SES está em modo **Sandbox**: só envia para identidades verificadas. Para produção é preciso solicitar o Production Access.

---

## 7. Segurança

Spring Security está habilitado (`core/security/WebSecurityConfig.java`). Estado atual:

| Aspecto | Configuração |
|---|---|
| Mecanismo de autenticação | HTTP Basic |
| Usuários | **em memória**, definidos em `configure(AuthenticationManagerBuilder)` |
| Hash de senha | `BCryptPasswordEncoder` (bean `passwordEncoder`) |
| Autorização | `/v1/cozinhas/**` é público; todas as demais rotas exigem autenticação |
| Sessão | `STATELESS` (sem sessão nem cookie) |
| CSRF | desabilitado (API REST) |

**Pontos de atenção:**

- A configuração usa `WebSecurityConfigurerAdapter`, que está **depreciado** no Spring Security 5.7 (usado pelo Boot 2.7). A forma recomendada é um bean `SecurityFilterChain`.
- Os usuários `thiago` e `joao` (senha `123`, papel `ADMIN`) estão **fixos no código**. Isso é apenas para estudo.
- As entidades `Usuario`, `Grupo` e `Permissao` existem no domínio, mas **ainda não são usadas na autenticação**. Para autenticar pelo banco, falta um `UserDetailsService` que leia `Usuario`.
- Não há controle de autorização por grupo/permissão (`@PreAuthorize` etc.) no estado atual.

---

## 8. Configuração e perfis

Arquivos principais:

- `application.properties`: datasource MySQL, Flyway, Jackson, mail, storage, email, OpenAI.
- `logback-spring.xml`: console e, no profile `production`, appender do Loggly.
- `flyway.properties`: configuração do plugin Flyway para Maven.

Propriedades sensíveis devem vir de variáveis de ambiente:

| Variável | Uso |
|---|---|
| `LOGGLY_TOKEN` | Token do Loggly |
| `OPENAI_API_KEY` | Chave da OpenAI |

> Pendências de segurança: a senha do banco (`spring.datasource.password`) e uma chave da OpenAI aparecem literalmente em `application.properties`. Recomenda-se movê-las para variáveis de ambiente.

**Profiles:** o Loggly só é ativado com `spring.profiles.active=production`.

---

## 9. Ferramentas de apoio

- **Testes:** `src/test`, com perfil `application-test.properties` e `spring-security-test` disponível.
- **Documentação da API:** Swagger UI via SpringFox, com anotações `*ControllerOpenApi` em `api/v1/openapi` e `api/v2`.
- **Relatórios:** PDF gerado por `PdfVendaReportService` (JasperReports).
- **Eventos:** publicação de eventos de domínio no agregado `Pedido`, tratados por listeners.
