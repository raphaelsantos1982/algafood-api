# algafood-auth

Servidor de autorização OAuth2. Emite o token de acesso que os clientes usam para chamar a API. Este serviço é uma aplicação separada da API, que roda na porta 8081 (o código não está neste repositório).

## Endpoint

**Base:** `http://localhost:8081`

| Método | Caminho | O que faz |
|---|---|---|
| POST | `/oauth/token` | Gera um token de acesso com o fluxo de senha (Resource Owner Password Credentials) |

## Requisição

**Autenticação do cliente:** HTTP Basic, com as credenciais do cliente OAuth2 cadastrado. As credenciais não são documentadas aqui.

**Corpo** (`application/x-www-form-urlencoded`)

| Campo | Valor | Descrição |
|---|---|---|
| `grant_type` | `password` | Fluxo usado |
| `username` | e-mail ou nome do usuário | Dono do recurso |
| `password` | senha do usuário | Senha do usuário |

> Os valores de `username` e `password` não são documentados aqui.

## Resposta (formato OAuth2 padrão, exemplo)

```json
{
  "access_token": "token-opaco-ou-jwt",
  "token_type": "bearer",
  "expires_in": 3600,
  "scope": "READ WRITE"
}
```

Use o `access_token` no cabeçalho `Authorization: Bearer <token>` das chamadas à API.

## Observações

- O fluxo de senha é o estudado no capítulo 22 do curso. Ele é adequado para aplicações próprias, e não para terceiros.
- A autenticação está sendo implementada conforme o avanço no curso. Este documento deve ser atualizado a cada etapa (ver `PROGRESSO-CURSO.md`).
