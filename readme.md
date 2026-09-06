# Migração de Clientes — Java + JDBC

Projeto de estudo para desenvolver uma rotina de **migração de dados de uma base legada em CSV para MySQL**, utilizando Java e JDBC.

O projeto tem como foco principal **entender o processo por baixo das abstrações**, praticando leitura de arquivos, validação, transformação de dados, acesso a banco e persistência.

---

## 🎯 Objetivo

Construir uma rotina capaz de:

* Ler registros de uma base CSV legada;
* Validar os dados recebidos;
* Transformar os dados para os tipos adequados;
* Persistir os registros no MySQL;
* Tratar registros inválidos e erros;
* Trabalhar com transações e segurança na persistência.

### Principal objetivo de aprendizado

Mais do que simplesmente fazer a migração funcionar, o objetivo é compreender o fluxo:

**Arquivo → Parsing → Validação → Transformação → JDBC → Banco de dados**

Posteriormente, o projeto será refatorado para uma estrutura mais organizada, aplicando conceitos de POO e boas práticas.

---

## Tecnologias

* Java 21
* JDBC
* MySQL
* Maven
* Git / GitHub

---

## Estrutura

```text
projeto/
├── src/
│   └── Main.java
├── base_legada/
│   └── clientes.csv
├── pom.xml
└── README.md
```

---

## Dados da base legada

A tabela `clientes` possui os seguintes campos:

| Campo      | Tipo conceitual |
| ---------- | --------------- |
| id         | Inteiro         |
| nome       | Texto           |
| email      | Texto           |
| cpf        | Texto           |
| telefone   | Texto           |
| senha      | Texto           |
| ativo      | Booleano        |
| created_at | Data/hora       |
| updated_at | Data/hora       |

O CSV possui **9 campos separados por `;`**.

---

## Processo de migração

### 1. Leitura

Utilização de `Path`, `BufferedReader` e `readLine()` para percorrer o arquivo linha por linha.

### 2. Parsing

Cada linha é dividida em campos e armazenada em um `String[]`.

Nesse estágio foi praticado:

* Arrays;
* Índices;
* `length`;
* `String`;
* `split()`.

### 3. Validação

Os registros são verificados antes de continuar o processamento.

Atualmente são validados:

* Quantidade de campos;
* Nome;
* Email;
* Senha.

### 4. Transformação

Os valores recebidos do CSV começam como `String` e precisam ser convertidos ou normalizados conforme o tipo esperado pelo banco.

Exemplo já praticado:

`String → int` utilizando `Integer.parseInt()`.

Próximas transformações:

* `ativo`;
* CPF;
* telefone;
* datas.

### 5. Persistência

A próxima etapa será utilizar JDBC para:

* Abrir uma conexão;
* Criar um `PreparedStatement`;
* Definir os parâmetros;
* Executar o `INSERT`;
* Trabalhar com transações.

---

## Conceitos estudados

* Manipulação de arquivos;
* `Path`;
* `BufferedReader`;
* Arrays;
* Strings;
* Loops;
* Validação;
* Parsing e conversão de tipos;
* Exceções verificadas (`IOException`);
* JDBC;
* SQL;
* Preparação para POO.

---

## Progresso

### Leitura

* [x] Localizar arquivo
* [x] Abrir arquivo
* [x] Ler linha por linha

### Parsing

* [x] Separar campos
* [x] Trabalhar com arrays
* [x] Entender índices
* [x] Utilizar `length`

### Validação

* [x] Validar quantidade de campos
* [x] Validar campos obrigatórios

### Transformação

* [x] Converter `id`
* [ ] Transformar `ativo`
* [ ] Normalizar CPF
* [ ] Normalizar telefone
* [ ] Normalizar datas

### Banco de dados

* [x] Conectar ao MySQL
* [ ] Criar `PreparedStatement`
* [ ] Inserir registros
* [ ] Trabalhar com transações
* [ ] Tratamento de erros

### Refatoração

* [ ] Aplicar POO
* [ ] Separar responsabilidades
* [ ] Criar classes
* [ ] Criar métodos
* [ ] Adicionar testes
* [ ] Melhorar arquitetura

---

## 📍 Estado atual

A etapa de **leitura, parsing e validação básica** já está funcionando.

Também foi realizada a primeira conversão de dados:

`String → int`

O próximo passo é continuar a etapa de **transformação dos dados**, começando pelo campo `ativo`.

---

## Filosofia do projeto

Este projeto está sendo desenvolvido com foco em **aprendizado e compreensão**, evitando abstrações prematuras.

A implementação será construída primeiro de forma simples para entender o funcionamento de cada etapa.

Depois que o fluxo estiver funcionando, o código será evoluído para uma estrutura mais profissional utilizando:

**POO → separação de responsabilidades → boas práticas → testes → arquitetura.**

O objetivo final não é apenas possuir um sistema de migração funcional, mas compreender **como e por que cada parte funciona**.
