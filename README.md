# AlgaFood API

API REST desenvolvida durante o curso **Especialista Spring REST** da **AlgaWorks**.

O projeto simula uma plataforma de delivery de restaurantes, permitindo o gerenciamento de restaurantes, produtos, pedidos, usuários e demais recursos da aplicação, além de integrar serviços da AWS para armazenamento de arquivos e envio de e-mails transacionais.

> **Status:** Em desenvolvimento (Curso Especialista Spring REST - AlgaWorks)

---

## Tecnologias utilizadas

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- Bean Validation
- Flyway
- MySQL
- Maven
- Amazon S3
- Amazon SES
- Spring Mail

> *Esta lista será atualizada conforme novas tecnologias forem sendo utilizadas no projeto.*

---

## Funcionalidades implementadas

- ✅ API REST para gerenciamento de restaurantes, produtos, pedidos, usuários e demais recursos da aplicação
- ✅ Upload de imagens para Amazon S3
- ✅ Envio de e-mails transacionais utilizando Amazon SES (SMTP)
- ✅ Migração de banco de dados com Flyway
- ✅ Tratamento global de exceções

> *Novas funcionalidades serão adicionadas conforme a evolução do curso.*

---

## Integrações AWS

### Amazon S3

Integração responsável pelo armazenamento das imagens dos produtos.

**Configurações realizadas:**

- Criação de buckets no Amazon S3
- Configuração de usuário IAM
- Geração de Access Key e Secret Access Key
- Configuração das credenciais AWS na aplicação
- Upload e remoção de imagens
- Configuração via AWS SDK
- Utilização de múltiplos buckets para estudos
- Armazenamento das fotos dos produtos

---

### Amazon SES

Integração responsável pelo envio de e-mails transacionais da aplicação.

**Configurações realizadas:**

- Criação de identidade de e-mail
- Verificação da identidade no Amazon SES
- Criação de credenciais SMTP
- Configuração do Spring Mail
- Integração com JavaMailSender
- Envio de e-mails HTML de confirmação de pedidos
- Configuração utilizando Amazon SES Sandbox para ambiente de testes

> **Observação:** Durante o desenvolvimento foi utilizada uma conta Amazon SES em modo **Sandbox**, configuração padrão para novas contas AWS. Nesse modo, o envio de e-mails é permitido apenas para identidades (endereços ou domínios) previamente verificadas. Para utilizar o serviço em produção e enviar e-mails para qualquer destinatário, é necessário solicitar à AWS a liberação do ambiente de produção (**Production Access**).

---

## Como executar

**Em construção.**

Esta seção será atualizada ao final do desenvolvimento do projeto com todas as instruções necessárias para executar a aplicação localmente.

---

## Próximos passos

- Continuação do desenvolvimento durante o curso Especialista Spring REST.
- Implementação das funcionalidades dos próximos capítulos.
- Atualização contínua deste README conforme a evolução do projeto.

---

## Autor

**Raphael Anderson dos Santos de Oliveira**

Projeto desenvolvido para fins de estudo e aperfeiçoamento em desenvolvimento Backend com Java, Spring Boot e serviços da AWS.