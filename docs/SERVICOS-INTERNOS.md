# Serviços internos

> **Este arquivo não traz schemas da API.** Ele descreve apenas as classes internas da aplicação: regras de negócio (`domain/service`) e implementações técnicas (`infrastructure/service`).
>
> Os endpoints e os schemas de entrada e saída de cada recurso estão em [`AlgaFood/`](AlgaFood/), com um arquivo por serviço da coleção AlgaWorks. Veja a lista completa no final deste documento.

---

## 1. Serviços de domínio

Ficam em `domain/service`. Cada cadastro segue o mesmo padrão:

- `salvar(entidade)`: cria ou atualiza, validando regras de negócio.
- `excluir(id)`: remove, bloqueando a exclusão se a entidade estiver em uso.
- `buscarOuFalhar(id)`: devolve a entidade ou lança exceção de "não encontrado" (404).

### 1.1 Cadastros

| Serviço | Responsabilidade | Principais operações |
|---|---|---|
| `CadastroCozinhaService` | Tipos de cozinha | `salvar`, `excluir`, `buscarOuFalhar` |
| `CadastroEstadoService` | Estados (UF) | `salvar`, `excluir`, `buscarOuFalhar` |
| `CadastroCidadeService` | Cidades, vinculadas a um estado | `salvar`, `excluir`, `buscarOuFalhar` |
| `CadastroFormaPagamentoService` | Formas de pagamento | `salvar`, `excluir`, `buscarOuFalhar` |
| `CadastroPermissaoService` | Permissões de acesso | `buscarOuFalhar` |
| `CadastroGrupoService` | Grupos de permissões | `salvar`, `excluir`, `buscarOuFalhar`, `associarPermissao`, `desassociarPermissao` |
| `CadastroUsuarioService` | Usuários | `salvar`, `buscarOuFalhar`, `alterarSenha`, `associarGrupo`, `desassociarGrupo` |
| `CadastroRestauranteService` | Restaurantes | `salvar`, `buscarOuFalhar`, `ativar`/`inativar` (um ou vários), `abrir`/`fechar`, `associarResponsavel`/`desassociarResponsavel`, `associarFormaPagamento`/`desassociarFormaPagamento` |
| `CadastroProdutoService` | Produtos de um restaurante | `salvar`, `buscarOuFalhar(restauranteId, produtoId)` |

**Regras de negócio relevantes**

- `CadastroUsuarioService.alterarSenha` exige a senha atual correta antes de definir a nova.
- `CadastroRestauranteService.ativar`/`inativar` aceitam uma lista de ids, para atualizar vários restaurantes de uma vez.
- `CadastroRestauranteService.abrir`/`fechar` controlam o campo `aberto` (se o restaurante está recebendo pedidos agora).
- As exclusões verificam se a entidade está em uso e lançam `EntidadeEmUsoException` (409) em vez de apagar.

### 1.2 Pedidos

| Serviço | Responsabilidade | Principais operações |
|---|---|---|
| `EmissaoPedidoService` | Cria um pedido novo | `emitir(pedido)`, `buscarOuFalhar(codigoPedido)` |
| `FluxoPedidoService` | Muda o status do pedido | `confirmar`, `cancelar`, `entregar` |

- Os pedidos são identificados pelo `codigo` (UUID), e não pelo `id` numérico. Isso evita expor a sequência de pedidos na URL.
- O fluxo de status está em `StatusPedido` (ver seção 2 do documento de arquitetura).
- `FluxoPedidoService.confirmar` e `cancelar` publicam eventos (`PedidoConfirmadoEvent`, `PedidoCanceladoEvent`). Os listeners de e-mail reagem a esses eventos.

### 1.3 Fotos de produtos

| Serviço | Responsabilidade | Principais operações |
|---|---|---|
| `CatalogoFotoProdutoService` | Salva, consulta e remove a foto de um produto | `salvar(foto, arquivo)`, `buscarOuFalhar(restauranteId, produtoId)`, `excluir(restauranteId, produtoId)` |
| `FotoStorageService` (interface) | Contrato de armazenamento de arquivos | `armazenar(NovaFoto)`, `recuperar(nome)`, `remover(nome)` |

`CatalogoFotoProdutoService` decide **o que** guardar (metadados no banco) e delega **onde** guardar ao `FotoStorageService`. Veja a seção 2.2 para as implementações.

### 1.4 Consultas e relatórios

| Serviço | Responsabilidade |
|---|---|
| `VendaQueryService` (interface) | Consulta de vendas agregadas por dia |
| `VendaReportService` (interface) | Gera o relatório de vendas diárias em PDF |
| `ChatGPTService` | Envia um prompt para a API da OpenAI e devolve o texto da resposta (`getChatGPTResponse`) |

### 1.5 E-mail

| Serviço | Responsabilidade |
|---|---|
| `EnvioEmailService` (interface) | Contrato de envio: recebe uma `Mensagem` |
| Listeners `NotificacaoClientePedidoConfirmadoListener` e `NotificacaoClientePedidoCanceladoListener` | Montam a mensagem e enviam ao cliente quando o pedido é confirmado ou cancelado |

---

## 2. Serviços de infraestrutura

Ficam em `infrastructure/service`. São as implementações concretas dos contratos de domínio.

### 2.1 E-mail (`infrastructure/service/email`)

Três implementações de `EnvioEmailService`, escolhidas pela propriedade `algafood.email.impl`:

| Implementação | Valor da propriedade | Comportamento |
|---|---|---|
| `FakeEnvioEmailService` | `fake` (padrão) | Não envia nada; só registra no log. Útil em desenvolvimento |
| `SandboxEnvioEmailService` | `sandbox` | Envia, mas redireciona todos os e-mails para `algafood.email.sandbox.destinatario` |
| `SmtpEnvioEmailService` | `smtp` | Envio real via SMTP (Amazon SES). Monta o HTML a partir de um template Freemarker |

Erros de envio viram `EmailException`.

### 2.2 Armazenamento de fotos (`infrastructure/service/storage`)

Implementações de `FotoStorageService`, escolhidas por `algafood.storage.tipo`:

| Implementação | Valor | Onde guarda |
|---|---|---|
| `LocalFotoStorageService` | `local` (padrão) | Pasta no disco, em `algafood.storage.local.diretorio-fotos` |
| `S3FotoStorageService` | `s3` | Bucket S3 `algafood.storage.s3.bucket` |
| `S3NovoFotoStorageService` | bucket de estudo 2 | Bucket `algafood.storage.s3-novo.bucket` |
| `S3TerceiroFotoStorageService` | bucket de estudo 3 | Bucket `algafood.storage.s3-terceiro.bucket` |

Os três buckets S3 são de estudo: a mesma interface foi implementada três vezes para praticar. Em produção, bastaria uma implementação.

Erros viram `StorageException`.

### 2.3 Relatórios e consultas (`infrastructure/service`)

| Classe | Responsabilidade |
|---|---|
| `PdfVendaReportService` | Gera o PDF de vendas diárias com JasperReports. Erros viram `ReportException` |
| `VendaQueryServiceImpl` | Faz a consulta agregada de vendas diárias por data, com filtro de restaurante e período. Recebe o `timeOffset` para ajustar o agrupamento ao fuso do cliente |

---

## 3. Erros

Todas as respostas de erro seguem o formato `Problem`, com o tipo do erro, o status HTTP e a mensagem:

```json
{
  "type": "https://algafood.com.br/recurso-nao-encontrado",
  "title": "Recurso não encontrado",
  "status": 404,
  "detail": "Não existe um cadastro de restaurante com código 99"
}
```

Os tipos de erro estão em `api/exceptionhandler/ProblemType.java`. Consulte a seção 5.4 da arquitetura para a lista.

---

## 4. Onde estão os schemas da API

| Pasta | Arquivo |
|---|---|
| `AlgaFood/` | Cozinhas, Estados, Cidades, Formas de Pagamento, Restaurantes, Produtos, Fotos de Produtos, Pedidos, Usuários, Grupos, Estatísticas, Documentação - api-docs |
| `Chatgpt/` | Chatgpt |
| `algafood-auth/` | algafood-auth |

Índice geral: [README.md](README.md).
