# Pedidos

Pedidos feitos pelos clientes nos restaurantes, com o ciclo de vida: criado → confirmado → entregue, ou cancelado.

**Base:** `/v1/pedidos`

| Método | Caminho | O que faz |
|---|---|---|
| GET | `/v1/pedidos` | Lista pedidos, com filtros e paginação |
| GET | `/v1/pedidos/{codigo}` | Busca um pedido pelo código |
| POST | `/v1/pedidos` | Cria um pedido |
| PUT | `/v1/pedidos/{codigo}/confirmacao` | Confirma o pedido |
| PUT | `/v1/pedidos/{codigo}/entrega` | Marca como entregue |
| PUT | `/v1/pedidos/{codigo}/cancelamento` | Cancela o pedido |

O pedido é identificado pelo `codigo` (UUID), não pelo id numérico.

## Filtros da listagem

| Nome | Exemplo | Descrição |
|---|---|---|
| `clienteId` | `2` | Pedidos de um cliente |
| `restauranteId` | `1` | Pedidos de um restaurante |
| `dataCriacaoInicio` / `dataCriacaoFim` | `2019-10-30T00:00:00Z` | Período |
| `campos` | `codigo,subTotal` | Retorna só os campos informados |
| `sort` | `codigo` | Ordenação |
| `size` / `page` | `2` / `0` | Paginação |

## Schemas

**Entrada — criar pedido**
```json
{
  "restaurante": { "id": 1 },
  "formaPagamento": { "id": 1 },
  "enderecoEntrega": {
    "cep": "38400-000",
    "logradouro": "Rua Floriano Peixoto",
    "numero": "600",
    "complemento": "Apto 704",
    "bairro": "Brasil",
    "cidade": { "id": 1 }
  },
  "itens": [
    { "produtoId": 1, "quantidade": 3, "observacao": "Sem pimenta" },
    { "produtoId": 2, "quantidade": 1 }
  ]
}
```

**Saída — pedido (resumido)**
```json
{
  "codigo": "f9981ca4-5a5e-4da3-af04-933861df3e55",
  "subtotal": 85.80,
  "taxaFrete": 6.50,
  "valorTotal": 92.30,
  "status": "CRIADO",
  "restaurante": { "id": 1, "nome": "Trattoria do Chef" },
  "cliente": { "id": 2, "nome": "Maria", "email": "maria@exemplo.com" }
}
```

Os valores de subtotal, frete e total são calculados pelo servidor. Não os envie na criação.

## Status

| Status | Pode passar para |
|---|---|
| CRIADO | CONFIRMADO, CANCELADO |
| CONFIRMADO | ENTREGUE |
| ENTREGUE | — |
| CANCELADO | — |

## Observações

- A coleção tem um caso que chama `/v1/pedidos/1/entrega` com o id numérico, e não com o código. Pela regra atual, isso não deve funcionar. Vale ajustar a requisição para usar o código.
- Confirmar, entregar e cancelar disparam e-mail para o cliente (via eventos de domínio).
