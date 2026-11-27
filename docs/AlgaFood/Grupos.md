# Grupos

Conjuntos de permissões (ex.: Administrador, Cozinheiro). Os usuários recebem permissões por meio dos grupos.

**Bases:** `/v1/grupos` e `/v1/grupos/{id}/permissoes`

## Grupos

| Método | Caminho | O que faz |
|---|---|---|
| GET | `/v1/grupos` | Lista todos |
| GET | `/v1/grupos/{id}` | Busca um grupo |
| POST | `/v1/grupos` | Cria um grupo |
| PUT | `/v1/grupos/{id}` | Atualiza o nome |
| DELETE | `/v1/grupos/{id}` | Remove o grupo |

## Permissões do grupo

| Método | Caminho | O que faz |
|---|---|---|
| GET | `/v1/grupos/{id}/permissoes` | Lista as permissões do grupo |
| PUT | `/v1/grupos/{id}/permissoes/{permissaoId}` | Associa uma permissão |
| DELETE | `/v1/grupos/{id}/permissoes/{permissaoId}` | Remove uma permissão |

## Schemas

**Entrada**
```json
{ "nome": "Assistente social" }
```

**Saída**
```json
{ "id": 1, "nome": "Administrador" }
```

## Observações

- Os usuários são associados a grupos em **Usuários**.
