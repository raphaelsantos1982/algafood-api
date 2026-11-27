# Documentação do AlgaFood API

Índice da documentação do projeto. Comece pela arquitetura e use as pastas para consultar cada serviço.

## Documentos gerais

| Arquivo | Conteúdo |
|---|---|
| [ARQUITETURA.md](ARQUITETURA.md) | Stack, pacotes, modelo de domínio, endpoints, integrações, segurança e configuração |
| [SERVICOS-INTERNOS.md](SERVICOS-INTERNOS.md) | Classes internas da aplicação (regras de negócio e infraestrutura). Não traz schemas da API |
| [PROGRESSO-CURSO.md](PROGRESSO-CURSO.md) | Etapa atual do curso e situação de cada aula |

## Serviços por coleção do Postman (AlgaWorks)

Cada pasta segue o nome da pasta na coleção **AlgaWorks**. Dentro dela, há um arquivo por serviço.

| Pasta | Serviços |
|---|---|
| [AlgaFood/](AlgaFood/) | Cozinhas, Estados, Cidades, Formas de Pagamento, Restaurantes, Produtos, Fotos de Produtos, Pedidos, Usuários, Grupos, Estatísticas, Documentação - api-docs |
| [Chatgpt/](Chatgpt/) | Integração com a OpenAI |
| [algafood-auth/](algafood-auth/) | Servidor de autorização OAuth2 |

## Pendências de segurança encontradas na revisão

- A coleção **AlgaWorks** tem chaves da OpenAI em texto puro nos campos de autenticação. Recomenda-se revogá-las e usar variáveis secretas do Postman.
- `application.properties` tem a senha do MySQL e uma chave da OpenAI. Recomenda-se movê-las para variáveis de ambiente.
