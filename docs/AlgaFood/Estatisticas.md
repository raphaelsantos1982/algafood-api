# Estatísticas

Consulta de vendas agregadas por dia, com opção de saída em JSON ou PDF.

**Base:** `/v1/estatisticas`

| Método | Caminho | O que faz |
|---|---|---|
| GET | `/v1/estatisticas/vendas-diarias` | Lista o total de vendas e faturamento por dia |

## Parâmetros (query)

| Nome | Obrigatório | Exemplo | Descrição |
|---|---|---|---|
| `restauranteId` | não | `1` | Filtra um restaurante |
| `dataCriacaoInicio` | não | `2019-10-30T00:00:00Z` | Início do período (ISO 8601) |
| `dataCriacaoFim` | não | `2019-11-04T23:59:59Z` | Fim do período (ISO 8601) |
| `timeOffset` | não | `-03:00` | Fuso usado para agrupar os dias |

## Formato de saída

- `Accept: application/json` → lista em JSON.
- `Accept: application/pdf` → relatório em PDF.

**Saída JSON (exemplo)**

```json
[
  { "data": "2019-11-01", "totalVendas": 12, "totalFaturado": 1085.40 }
]
```

## Observações

- A coleção envia `Accept: application/pdf, application/json`, então o servidor escolhe o formato pelo cabeçalho.
