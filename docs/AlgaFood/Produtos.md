# Produtos

Itens do cardápio de cada restaurante. Um produto sempre pertence a um restaurante.

**Base:** `/v1/restaurantes/{restauranteId}/produtos`

| Método | Caminho | O que faz |
|---|---|---|
| GET | `/v1/restaurantes/{id}/produtos?incluirInativos=true` | Lista os produtos (inativos só se `incluirInativos=true`) |
| GET | `/v1/restaurantes/{id}/produtos/{produtoId}` | Busca um produto |
| POST | `/v1/restaurantes/{id}/produtos` | Cria um produto no restaurante |
| PUT | `/v1/restaurantes/{id}/produtos/{produtoId}` | Atualiza um produto |

## Schemas

**Entrada**
```json
{
  "nome": "Frango Agridoce",
  "descricao": "Frango agridoce com especiarias",
  "preco": 58.95,
  "ativo": false
}
```

**Saída**
```json
{
  "id": 10,
  "nome": "Frango Agridoce",
  "descricao": "Frango agridoce com especiarias",
  "preco": 58.95,
  "ativo": false
}
```

## Observações

- A coleção tem um caso com preço negativo (`-68.69`) para testar a validação. A resposta esperada é erro 400.
- Foto do produto: ver **Fotos de Produtos**.
