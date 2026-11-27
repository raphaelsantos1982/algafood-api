# Cidades

Cidades de cada estado. Servem de endereço dos restaurantes e dos pedidos.

Existem duas versões da API, e a coleção mostra duas formas de escolher a versão:

- **Por URI:** `/v1/cidades` ou `/v2/cidades`.
- **Por media type:** o caminho `/cidades` com o cabeçalho `Accept: application/vnd.algafood.v1+json` ou `...v2+json`.

> A listagem da v1 está **depreciada**. A resposta traz um cabeçalho avisando a substituição pela v2.

## Operações

| Método | Caminho (v1) | Caminho (v2) | O que faz |
|---|---|---|---|
| GET | `/v1/cidades` | `/v2/cidades` | Lista |
| GET | `/v1/cidades/{id}` | `/v2/cidades/{id}` | Busca uma cidade |
| POST | `/v1/cidades` | `/v2/cidades` | Cria uma cidade |
| PUT | `/cidades/{id}` | `/cidades/{id}` | Atualiza (a coleção usa o caminho sem versão) |
| DELETE | `/cidades/{id}` | `/cidades/{id}` | Remove a cidade |

## Schemas

**v1 — entrada**
```json
{
  "nome": "Ouro Preto",
  "estado": { "id": 1 }
}
```

**v1 — saída**
```json
{
  "id": 1,
  "nome": "Ouro Preto",
  "estado": { "id": 1, "nome": "Minas Gerais" }
}
```

**v2 — entrada**
```json
{
  "nomeCidade": "Contagem",
  "idEstado": 1
}
```

**v2 — saída**
```json
{
  "idCidade": 1,
  "nomeCidade": "Contagem",
  "idEstado": 1,
  "nomeEstado": "Minas Gerais"
}
```

## Observações

- Na v2 os campos são planos (`nomeCidade`, `idEstado`) e não aninhados como na v1.
- Os caminhos sem versão (`/cidades`) dependem do cabeçalho `Accept` para escolher a versão. Confirme esse comportamento antes de usar em produção.
