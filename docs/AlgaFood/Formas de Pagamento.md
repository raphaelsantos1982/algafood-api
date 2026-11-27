# Formas de Pagamento

Meios de pagamento aceitos (ex.: Pix, cartão de crédito). Os restaurantes escolhem quais aceitam.

**Base:** `/v1/formas-pagamento`

| Método | Caminho | O que faz |
|---|---|---|
| GET | `/v1/formas-pagamento` | Lista todas |
| GET | `/v1/formas-pagamento/{id}` | Busca uma forma |
| POST | `/v1/formas-pagamento` | Cria uma forma |
| PUT | `/v1/formas-pagamento/{id}` | Atualiza a descrição |
| DELETE | `/v1/formas-pagamento/{id}` | Remove (bloqueado se estiver em uso) |

## Schemas

**Entrada**
```json
{ "descricao": "Pix" }
```

**Saída**
```json
{ "id": 1, "descricao": "Pix" }
```

## Observações

- Para associar uma forma de pagamento a um restaurante, use os endpoints de **Restaurantes**.
