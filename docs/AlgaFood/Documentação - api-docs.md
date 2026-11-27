# Documentação - api-docs

Endpoints de apoio: documentação técnica da API e ponto de entrada com os links dos recursos.

**Base:** `http://localhost:8080`

| Método | Caminho | O que faz |
|---|---|---|
| GET | `/v3/api-docs` | Devolve o JSON OpenAPI da API (gerado pelo SpringFox) |
| GET | `/v1` | Root Entry Point: lista os links HAL de todos os recursos |

## Saída de `/v1` (exemplo resumido)

```json
{
  "_links": {
    "cozinhas": { "href": "http://localhost:8080/v1/cozinhas" },
    "pedidos": { "href": "http://localhost:8080/v1/pedidos" }
  }
}
```

## Observações

- Segundo a configuração de segurança atual (`WebSecurityConfig`), apenas `/v1/cozinhas/**` é público. Por isso, estas requisições precisam de autenticação; a coleção as envia sem credenciais.
- A aba Swagger UI usa o JSON de `/v3/api-docs`.
