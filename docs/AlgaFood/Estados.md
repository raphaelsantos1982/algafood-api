# Estados

Unidades federativas (ex.: Minas Gerais). As cidades pertencem a um estado.

**Base:** `/v1/estados`

| Método | Caminho | O que faz |
|---|---|---|
| GET | `/v1/estados` | Lista todos |
| GET | `/v1/estados/{id}` | Busca um estado |
| POST | `/v1/estados` | Cria um estado |
| PUT | `/v1/estados/{id}` | Atualiza o nome |
| DELETE | `/v1/estados/{id}` | Remove (bloqueado se houver cidades vinculadas) |

## Schemas

**Entrada**
```json
{ "nome": "Santa Catarina" }
```

**Saída**
```json
{ "id": 1, "nome": "Minas Gerais" }
```
