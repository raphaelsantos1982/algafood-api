# Chatgpt

Integração com a API da OpenAI. Envia um texto (prompt) e devolve a resposta do modelo.

## Endpoint da aplicação

**Base:** `/api/chat`

| Método | Caminho | O que faz |
|---|---|---|
| POST | `/api/chat/ask` | Envia o prompt e devolve a resposta em texto |

**Entrada:** o corpo é apenas o texto do prompt (`String`), não um objeto JSON.

```
"Qual é a capital do Brasil?"
```

**Saída:** texto com a resposta do modelo.

## Requisição direta à OpenAI (teste)

A coleção também tem uma chamada direta ao serviço externo, usada para comparar.

| Método | URL | Autenticação |
|---|---|---|
| POST | `https://api.openai.com/v1/responses` | Bearer token |

```json
{
  "model": "gpt-5-nano",
  "input": "Explique o que é Spring Boot."
}
```

## Configuração

- A chave da API fica em `openai.api.key` (variável `OPENAI_API_KEY`) e a URL em `openai.api.url`.

## Observações — segurança

- **Ação necessária:** a coleção tem chaves da OpenAI escritas em texto puro, em dois lugares: no campo de autenticação e no cabeçalho `Authorization` da requisição "Teste chamada Chat - PRD". Essas chaves devem ser revogadas no painel da OpenAI e substituídas por uma variável secreta do Postman (ver skill `postman:variables`).
- Os valores não são repetidos nesta documentação.
