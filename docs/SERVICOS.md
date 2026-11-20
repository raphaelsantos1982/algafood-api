# Serviços e schemas do AlgaFood API

Este documento lista os serviços da aplicação, o que cada um faz e como são os dados que entram e saem da API.

Ele tem três partes:

1. **Serviços de domínio**: regras de negócio, usadas pelos controllers.
2. **Serviços de infraestrutura**: implementações técnicas (S3, disco, e-mail, PDF, consultas SQL).
3. **Schemas**: formato JSON de entrada e saída de cada recurso da API v1.

> Para a visão geral do projeto (camadas, pacotes e integrações), veja [ARQUITETURA.md](ARQUITETURA.md).
>
> **Atualize este arquivo** quando criar, remover ou alterar um serviço ou um recurso da API.

---

## Como ler este documento

- **Entrada** é o JSON que o cliente envia (body de POST/PUT/PATCH).
- **Saída** é o JSON que a API devolve. Recursos trazem campos `_links` (HATEOAS) que não estão nos exemplos, para deixar o formato mais limpo.
- Campos marcados com `id` são chaves numéricas (`Long`). Os demais tipos seguem a convenção:
  - `Long`/`Integer`: número inteiro
  - `BigDecimal`: número decimal (valores monetários)
  - `Boolean`: `true` ou `false`
  - `OffsetDateTime`: data e hora no formato ISO 8601 (ex.: `2026-11-20T10:30:00Z`)
- Exemplos usam valores fictícios.

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

## 3. Schemas da API v1

Os schemas abaixo são os usados pelos controllers em `api/v1`. A tabela de endpoints está em [ARQUITETURA.md](ARQUITETURA.md#52-endpoints-v1).

### 3.1 Cozinha

**Saída** (`CozinhaModel`)
```json
{ "id": 1, "nome": "Italiana" }
```

**Entrada** (`CozinhaInput`)
```json
{ "nome": "Italiana" }
```

### 3.2 Estado

**Saída** (`EstadoModel`)
```json
{ "id": 1, "nome": "Minas Gerais" }
```

**Entrada** (`EstadoInput`)
```json
{ "nome": "Minas Gerais" }
```

### 3.3 Cidade

**Saída** (`CidadeModel`)
```json
{
  "id": 1,
  "nome": "Uberlândia",
  "estado": { "id": 1, "nome": "Minas Gerais" }
}
```

**Saída resumida** (`CidadeResumoModel`, usada dentro de `Endereco`)
```json
{ "id": 1, "nome": "Uberlândia", "estado": "Minas Gerais" }
```

**Entrada** (`CidadeInput`)
```json
{
  "nome": "Uberlândia",
  "estado": { "id": 1 }
}
```

### 3.4 Forma de pagamento

**Saída** (`FormaPagamentoModel`)
```json
{ "id": 1, "descricao": "Cartão de crédito" }
```

**Entrada** (`FormaPagamentoInput`)
```json
{ "descricao": "Cartão de crédito" }
```

### 3.5 Permissão

**Saída** (`PermissaoModel`)
```json
{
  "id": 1,
  "nome": "CONSULTAR_COZINHAS",
  "descricao": "Permite consultar cozinhas"
}
```

### 3.6 Grupo

**Saída** (`GrupoModel`)
```json
{ "id": 1, "nome": "Administrador" }
```

**Entrada** (`GrupoInput`)
```json
{ "nome": "Administrador" }
```

### 3.7 Usuário

**Saída** (`UsuarioModel`). A senha nunca é devolvida.
```json
{
  "id": 1,
  "nome": "João da Silva",
  "email": "joao@algafood.com"
}
```

**Entrada** (`UsuarioInput`), para atualizar dados sem senha
```json
{
  "nome": "João da Silva",
  "email": "joao@algafood.com"
}
```

**Entrada com senha** (`UsuarioComSenhaInput`), para cadastrar
```json
{
  "nome": "João da Silva",
  "email": "joao@algafood.com",
  "senha": "senha-forte-aqui"
}
```

**Alteração de senha** (`SenhaInput`)
```json
{
  "senhaAtual": "senha-antiga",
  "novaSenha": "senha-nova"
}
```

### 3.8 Endereço

Aparece dentro de restaurantes e pedidos.

**Saída** (`EnderecoModel`)
```json
{
  "cep": "38400-000",
  "logradouro": "Rua das Flores",
  "numero": "100",
  "complemento": "Apto 201",
  "bairro": "Centro",
  "cidade": { "id": 1, "nome": "Uberlândia", "estado": "Minas Gerais" }
}
```

**Entrada** (`EnderecoInput`). A cidade é informada só pelo id.
```json
{
  "cep": "38400-000",
  "logradouro": "Rua das Flores",
  "numero": "100",
  "complemento": "Apto 201",
  "bairro": "Centro",
  "cidade": { "id": 1 }
}
```

### 3.9 Restaurante

**Saída completa** (`RestauranteModel`)
```json
{
  "id": 1,
  "nome": "Trattoria do Chef",
  "taxaFrete": 6.50,
  "ativo": true,
  "aberto": false,
  "cozinha": { "id": 1, "nome": "Italiana" },
  "endereco": {
    "cep": "38400-000",
    "logradouro": "Rua das Flores",
    "numero": "100",
    "complemento": null,
    "bairro": "Centro",
    "cidade": { "id": 1, "nome": "Uberlândia", "estado": "Minas Gerais" }
  }
}
```

**Saída básica** (`RestauranteBasicoModel`), usada em listagens
```json
{
  "id": 1,
  "nome": "Trattoria do Chef",
  "taxaFrete": 6.50,
  "cozinha": { "id": 1, "nome": "Italiana" }
}
```

**Saída de nome** (`RestauranteApenasNomeModel`), usada dentro de pedidos
```json
{ "id": 1, "nome": "Trattoria do Chef" }
```

**Entrada** (`RestauranteInput`)
```json
{
  "nome": "Trattoria do Chef",
  "taxaFrete": 6.50,
  "cozinha": { "id": 1 },
  "endereco": {
    "cep": "38400-000",
    "logradouro": "Rua das Flores",
    "numero": "100",
    "complemento": null,
    "bairro": "Centro",
    "cidade": { "id": 1 }
  }
}
```

### 3.10 Produto

**Saída** (`ProdutoModel`)
```json
{
  "id": 10,
  "nome": "Lasanha à bolonhesa",
  "descricao": "Massa fresca com molho da casa",
  "preco": 42.90,
  "ativo": true
}
```

**Entrada** (`ProdutoInput`)
```json
{
  "nome": "Lasanha à bolonhesa",
  "descricao": "Massa fresca com molho da casa",
  "preco": 42.90,
  "ativo": true
}
```

### 3.11 Foto do produto

**Saída** (`FotoProdutoModel`)
```json
{
  "nomeArquivo": "10-foto.jpg",
  "descricao": "Foto de divulgação",
  "contentType": "image/jpeg",
  "tamanho": 183224
}
```

**Entrada** (`FotoProdutoInput`), envio como `multipart/form-data`
- `arquivo`: o arquivo da imagem (validado por tipo e tamanho)
- `descricao`: texto opcional

### 3.12 Pedido

**Saída completa** (`PedidoModel`)
```json
{
  "codigo": "3f2c9d8e-1a2b-4c5d-9e0f-123456789abc",
  "subtotal": 85.80,
  "taxaFrete": 6.50,
  "valorTotal": 92.30,
  "status": "CRIADO",
  "dataCriacao": "2026-11-20T14:05:00Z",
  "dataConfirmacao": null,
  "dataEntrega": null,
  "dataCancelamento": null,
  "restaurante": { "id": 1, "nome": "Trattoria do Chef" },
  "cliente": { "id": 2, "nome": "Maria", "email": "maria@exemplo.com" },
  "formaPagamento": { "id": 1, "descricao": "Cartão de crédito" },
  "enderecoEntrega": {
    "cep": "38400-000",
    "logradouro": "Rua das Flores",
    "numero": "100",
    "complemento": "Apto 201",
    "bairro": "Centro",
    "cidade": { "id": 1, "nome": "Uberlândia", "estado": "Minas Gerais" }
  },
  "itens": [
    {
      "produtoId": 10,
      "produtoNome": "Lasanha à bolonhesa",
      "quantidade": 2,
      "precoUnitario": 42.90,
      "precoTotal": 85.80,
      "observacao": "Sem cebola"
    }
  ]
}
```

**Saída resumida** (`PedidoResumoModel`), usada na listagem
```json
{
  "codigo": "3f2c9d8e-1a2b-4c5d-9e0f-123456789abc",
  "subtotal": 85.80,
  "taxaFrete": 6.50,
  "valorTotal": 92.30,
  "status": "CRIADO",
  "dataCriacao": "2026-11-20T14:05:00Z",
  "restaurante": { "id": 1, "nome": "Trattoria do Chef" },
  "cliente": { "id": 2, "nome": "Maria", "email": "maria@exemplo.com" }
}
```

**Entrada** (`PedidoInput`) para criar um pedido. Não é preciso enviar valores: o servidor calcula subtotal, frete e total.
```json
{
  "restaurante": { "id": 1 },
  "formaPagamento": { "id": 1 },
  "enderecoEntrega": {
    "cep": "38400-000",
    "logradouro": "Rua das Flores",
    "numero": "100",
    "complemento": "Apto 201",
    "bairro": "Centro",
    "cidade": { "id": 1 }
  },
  "itens": [
    { "produtoId": 10, "quantidade": 2, "observacao": "Sem cebola" }
  ]
}
```

**Valores possíveis de `status`**

| Valor | Significado | Transições permitidas |
|---|---|---|
| `CRIADO` | Pedido recém-criado | → `CONFIRMADO`, → `CANCELADO` |
| `CONFIRMADO` | Restaurante aceitou | → `ENTREGUE` |
| `ENTREGUE` | Pedido entregue | — |
| `CANCELADO` | Pedido cancelado | — |

### 3.13 Estatísticas

**Filtro de vendas** (`VendaDiariaFilter`), usado como query params
| Campo | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `restauranteId` | Long | não | Restringe a um restaurante |
| `dataCriacaoInicio` | OffsetDateTime | não | Início do período |
| `dataCriacaoFim` | OffsetDateTime | não | Fim do período |

**Saída de vendas diárias** (`VendaDiaria`)
```json
[
  {
    "data": "2026-11-20",
    "totalVendas": 12,
    "totalFaturado": 1085.40
  }
]
```

### 3.14 Chat (ChatGPT)

**Entrada** (`ChatGPTInput`), para `POST /api/chat/ask`
```json
{
  "model": "gpt-3.5-turbo",
  "prompt": "Sugira um nome para uma pizzaria",
  "max_tokens": 100
}
```

---

## 4. Erros

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
