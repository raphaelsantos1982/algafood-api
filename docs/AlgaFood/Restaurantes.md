# Restaurantes

Estabelecimentos que vendem os produtos. Concentra também ativação, funcionamento (aberto/fechado), formas de pagamento aceitas e responsáveis.

**Base:** `/v1/restaurantes`

## Cadastro

| Método | Caminho | O que faz |
|---|---|---|
| GET | `/v1/restaurantes` | Lista (aceita `projecao=apenas-nome`) |
| GET | `/v1/restaurantes/{id}` | Busca um restaurante |
| POST | `/v1/restaurantes` | Cria um restaurante |
| PUT | `/v1/restaurantes/{id}` | Substitui os dados |
| PATCH | `/v1/restaurantes/{id}` | Altera só os campos enviados |

## Ativação

| Método | Caminho | O que faz |
|---|---|---|
| PUT | `/v1/restaurantes/{id}/ativo` | Ativa um restaurante |
| DELETE | `/v1/restaurantes/{id}/ativo` | Inativa um restaurante |
| PUT | `/v1/restaurantes/ativacoes` | Ativa vários (corpo: lista de ids) |
| DELETE | `/v1/restaurantes/ativacoes` | Inativa vários (corpo: lista de ids) |

## Funcionamento

| Método | Caminho | O que faz |
|---|---|---|
| PUT | `/v1/restaurantes/{id}/abertura` | Marca como aberto |
| PUT | `/v1/restaurantes/{id}/fechamento` | Marca como fechado |

## Formas de pagamento

| Método | Caminho | O que faz |
|---|---|---|
| GET | `/v1/restaurantes/{id}/formas-pagamento` | Lista as formas aceitas |
| PUT | `/v1/restaurantes/{id}/formas-pagamento/{formaId}` | Associa uma forma |
| DELETE | `/v1/restaurantes/{id}/formas-pagamento/{formaId}` | Desassocia uma forma |

## Responsáveis

| Método | Caminho | O que faz |
|---|---|---|
| GET | `/v1/restaurantes/{id}/responsaveis` | Lista os usuários responsáveis |
| PUT | `/v1/restaurantes/{id}/responsaveis/{usuarioId}` | Associa um responsável |
| DELETE | `/v1/restaurantes/{id}/responsaveis/{usuarioId}` | Remove um responsável |

## Schemas

**Entrada — criar/atualizar**
```json
{
  "nome": "Cozinha Mineira",
  "taxaFrete": 12,
  "cozinha": { "id": 1 },
  "endereco": {
    "cep": "38400-999",
    "logradouro": "Rua Afonso Pena",
    "numero": "1500",
    "complemento": null,
    "bairro": "Centro",
    "cidade": { "id": 2 }
  }
}
```

**Entrada — atualização parcial (PATCH)**
```json
{ "taxaFrete": 5 }
```

**Saída**
```json
{
  "id": 1,
  "nome": "Bom Prato",
  "taxaFrete": 5.00,
  "ativo": true,
  "aberto": false,
  "cozinha": { "id": 2, "nome": "Brasileira" },
  "endereco": {
    "cep": "38400-999",
    "logradouro": "Rua Afonso Pena",
    "numero": "2000",
    "complemento": null,
    "bairro": "Centro",
    "cidade": { "id": 1, "nome": "Uberlândia", "estado": "Minas Gerais" }
  }
}
```

**Ativação em lote — corpo**
```json
[1, 3, 4]
```

## Observações

- A coleção tem casos com valores inválidos (taxa de frete negativa). Eles servem para testar as validações e devem retornar erro 400.
- Para inativar, a coleção usa `DELETE /ativo`. Para ativar, `PUT /ativo`. Os dois existem no código.
