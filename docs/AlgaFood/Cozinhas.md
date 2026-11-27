# Cozinhas

Tipos de cozinha dos restaurantes (ex.: italiana, japonesa). Existem duas versões da API, que diferem nos nomes dos campos.

**Bases:** `/v1/cozinhas` e `/v2/cozinhas`

| Método | Caminho | O que faz |
|---|---|---|
| GET | `/v1/cozinhas?size=&page=` | Lista paginada |
| GET | `/v1/cozinhas/{id}` | Busca uma cozinha |
| POST | `/v1/cozinhas` | Cria uma cozinha |
| PUT | `/v1/cozinhas/{id}` | Atualiza uma cozinha |
| DELETE | `/v1/cozinhas/{id}` | Remove (bloqueado se houver restaurantes usando) |

A v2 tem as mesmas operações em `/v2/cozinhas`, com os campos renomeados.

## Schemas

**v1 — entrada (criar/atualizar)**
```json
{ "nome": "Argentina" }
```

**v1 — saída**
```json
{ "id": 1, "nome": "Argentina" }
```

**v2 — entrada**
```json
{ "nomeCozinha": "Argentina" }
```

**v2 — saída**
```json
{ "idCozinha": 1, "nomeCozinha": "Argentina" }
```

## Observações

- Na v2 o id sai como `idCozinha` e o nome como `nomeCozinha`. Quem migra da v1 precisa trocar esses campos.
- A coleção tem as duas versões lado a lado para comparação.
