# Fotos de Produtos

Imagem de divulgação de cada produto. O arquivo é guardado no armazenamento configurado (disco local ou Amazon S3).

**Base:** `/v1/restaurantes/{restauranteId}/produtos/{produtoId}/foto`

| Método | Caminho | O que faz |
|---|---|---|
| GET | `.../foto` | Busca a foto (`Accept: image/jpeg`) ou os dados dela (`Accept: application/json`) |
| PUT | `.../foto` | Envia ou substitui a foto (multipart) |
| DELETE | `.../foto` | Remove a foto |

## Envio (PUT)

Formato `multipart/form-data`:

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `arquivo` | arquivo | sim | Imagem (tipo e tamanho validados) |
| `descricao` | texto | não | Legenda da foto |

## Schema de saída (JSON)

```json
{
  "nomeArquivo": "10-foto.jpg",
  "descricao": "Descrição foto",
  "contentType": "image/jpeg",
  "tamanho": 183224
}
```

## Observações

- Os tipos de imagem aceitos e o tamanho máximo estão definidos nas validações `@FileContentType` e `@FileSize`.
- Para o `GET` da imagem, envie `Accept: image/jpeg`; para os dados, `application/json`.
