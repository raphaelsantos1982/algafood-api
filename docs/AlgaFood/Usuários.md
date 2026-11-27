# Usuários

Pessoas que usam o sistema: clientes, responsáveis por restaurantes e administradores. Cada usuário pode pertencer a vários grupos.

**Base:** `/v1/usuarios`

## Usuários

| Método | Caminho | O que faz |
|---|---|---|
| GET | `/v1/usuarios` | Lista todos |
| GET | `/v1/usuarios/{id}` | Busca um usuário |
| POST | `/v1/usuarios` | Cria um usuário (com senha) |
| PUT | `/v1/usuarios/{id}` | Atualiza nome e e-mail (sem senha) |
| PUT | `/v1/usuarios/{id}/senha` | Troca a senha (exige a senha atual) |
| DELETE | `/v1/usuarios/{id}` | Remove o usuário |

## Grupos do usuário

| Método | Caminho | O que faz |
|---|---|---|
| GET | `/v1/usuarios/{id}/grupos` | Lista os grupos do usuário |
| PUT | `/v1/usuarios/{id}/grupos/{grupoId}` | Adiciona o usuário a um grupo |
| DELETE | `/v1/usuarios/{id}/grupos/{grupoId}` | Remove o usuário do grupo |

## Schemas

**Entrada — cadastro (com senha)**
```json
{
  "nome": "Maria Silva",
  "email": "maria@exemplo.com",
  "senha": "troque-esta-senha"
}
```

**Entrada — atualização (sem senha)**
```json
{
  "nome": "Maria Silva",
  "email": "maria@exemplo.com.br"
}
```

**Entrada — troca de senha**
```json
{
  "senhaAtual": "senha-atual",
  "novaSenha": "senha-nova"
}
```

**Saída** (a senha nunca é devolvida)
```json
{
  "id": 1,
  "nome": "Maria Silva",
  "email": "maria@exemplo.com"
}
```

## Observações

- A coleção usa senhas de teste simples. Não use essas senhas em outros ambientes.
- A autenticação HTTP Basic da API hoje usa usuários fixos em memória (`WebSecurityConfig`), não esta tabela. Ver **algafood-auth** para o fluxo OAuth2.
