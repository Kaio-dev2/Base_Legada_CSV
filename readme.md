# Migração de Base Legada — Java + JDBC

Projeto de estudo para desenvolver uma rotina de migração de dados de uma base legada em CSV para MySQL, utilizando Java e JDBC.

O foco principal é compreender o processo por baixo das abstrações, praticando leitura de arquivos, parsing, validação, transformação de dados, acesso ao banco e persistência.

## Objetivo

Construir uma rotina capaz de:

- Ler registros de uma base CSV legada
- Validar os dados recebidos
- Transformar os dados para os tipos adequados
- Persistir os registros no MySQL
- Tratar registros inválidos e erros
- Trabalhar posteriormente com transações e segurança na persistência

### Principal objetivo de aprendizado

Mais do que simplesmente fazer a migração funcionar, o objetivo é compreender o fluxo:

**Arquivo → Parsing → Validação → Transformação → JDBC → Banco de dados**

A implementação começa de forma simples para permitir a compreensão de cada etapa. Posteriormente, o projeto será refatorado aplicando conceitos de POO, separação de responsabilidades, testes e boas práticas.

## Tecnologias

- Java 21
- JDBC
- MySQL
- Maven
- Git / GitHub

## Estrutura atual

```
Base_Legada_CSV/
├── base_legada/
│   ├── usuarios.csv
│   ├── clientes.csv
│   ├── enderecos.csv
│   ├── categorias.csv
│   ├── produtos.csv
│   ├── produto_imagens.csv
│   ├── estoques.csv
│   ├── carrinhos.csv
│   ├── pedidos.csv
│   └── pedido_itens.csv
│
├── src/
│   └── main/
│       └── java/
│           └── br/
│               └── com/
│                   └── loja/
│                       ├── Main.java
│                       ├── conexao/
│                       ├── migracao/
│                       └── util/
│
├── pom.xml
└── README.md
```

A estrutura separa a aplicação principal, conexão com o banco, rotinas de migração e futuras classes utilitárias.

## Dados da base legada

A base legada é composta por diferentes arquivos CSV relacionados ao funcionamento de uma loja virtual:

- usuarios.csv
- clientes.csv
- enderecos.csv
- categorias.csv
- produtos.csv
- produto_imagens.csv
- estoques.csv
- carrinhos.csv
- pedidos.csv
- pedido_itens.csv

Os arquivos utilizam `;` como separador e possuem uma linha de cabeçalho.

A análise da base é feita progressivamente, identificando:

- Estrutura dos registros
- Tipos de dados
- Campos obrigatórios e opcionais
- Formatos inconsistentes
- Registros duplicados
- Dependências entre tabelas
- Problemas a tratar durante a migração

## Processo de migração

### 1. Leitura

Utilização de `Path`, `Files`, `BufferedReader` e `readLine()` para acessar o arquivo e percorrê-lo linha por linha. O cabeçalho é lido separadamente antes do processamento dos registros.

### 2. Parsing

Cada linha do CSV é separada em campos e armazenada em um `String[]`.

Práticas envolvidas:

- Arrays
- Índices
- `length`
- `String`
- `split()`
- Percorrimento de registros

A leitura considera `;` como separador.

### 3. Validação

Antes da persistência, os registros passam por validações básicas. No fluxo de `clientes.csv`, são verificados:

- Quantidade correta de campos
- Campos obrigatórios
- Integridade necessária para as conversões
- Erros encontrados durante o processamento

Quando um registro apresenta problema, ele é rejeitado sem interromper a migração dos demais.

### 4. Transformação

Os valores do CSV são inicialmente tratados como `String` e depois convertidos para os tipos esperados pela aplicação e pelo banco.

Transformações já implementadas:

- `String` → `int`
- `String` → `boolean`
- `String` → `LocalDate`
- `String` → `LocalDateTime`

O campo `ativo` pode ter diferentes representações na base legada, como `1`, `0`, `S`, `N`, que são convertidas para um valor booleano antes da persistência.

As datas também são convertidas para `LocalDateTime`, tratando diferentes formatos encontrados na análise da base.

### 5. Persistência

Realizada via JDBC:

- Abrir conexão com o MySQL
- Criar um `PreparedStatement`
- Definir os parâmetros
- Executar o `INSERT`
- Tratar possíveis erros
- Continuar o processamento dos próximos registros
- Conferir os dados persistidos no banco

O `PreparedStatement` permite trabalhar com parâmetros separados da instrução SQL, evitando a construção direta dos valores na query.

## Primeira migração — Clientes

Migração completa realizada com `clientes.csv → tabela clientes`. O arquivo possui 60 registros.

Resultado da execução:

```
Total lidas: 60
Inseridas: 59
Rejeitadas: 1
```

### Registro problemático

Foi identificado um registro com email duplicado. O registro de ID 41 foi rejeitado devido à restrição de unicidade do campo `email` na tabela `clientes`.

O banco retornou:

```
Duplicate entry 'juliana.prado@email.com'
```

O erro não interrompeu a migração — os registros seguintes continuaram sendo processados normalmente até o ID 60, demonstrando o tratamento de erros por registro implementado na rotina.

## Fluxo atual

```
clientes.csv
     ↓
Leitura do arquivo
     ↓
Parsing da linha
     ↓
Validação
     ↓
Conversão dos dados
     ↓
PreparedStatement
     ↓
INSERT no MySQL
     ↓
Sucesso ou rejeição do registro
     ↓
Próximo registro
```

Resumo ao final da execução:

```
===== RESUMO DA MIGRAÇÃO =====
Total lidas: 60
Inseridas: 59
Rejeitadas: 1
```

## Conceitos estudados

**Manipulação de arquivos**
- `Path`, `Files`, `BufferedReader`, `readLine()`

**Estruturas da linguagem**
- Arrays, `String`, índices, `length`, loops, condicionais, métodos

**Validação e tratamento**
- Validação de quantidade de campos e campos obrigatórios
- `try/catch`, `SQLException`, `RuntimeException`
- Tratamento de erro por registro

**Conversão de dados**
- `Integer.parseInt()`, conversão para `boolean`
- `LocalDate`, `LocalDateTime`, `DateTimeFormatter`, `DateTimeParseException`

**JDBC**
- `Connection`, `DriverManager`, `PreparedStatement`
- `setInt()`, `setString()`, `setBoolean()`, `setObject()`, `executeUpdate()`

**Banco de dados**
- SQL, `INSERT`, chaves primárias, restrições de unicidade
- Persistência e validação dos registros após a migração

## Progresso

**Leitura**
- [x] Localizar arquivo
- [x] Abrir arquivo
- [x] Ler linha por linha
- [x] Ignorar cabeçalho

**Parsing**
- [x] Separar campos
- [x] Trabalhar com arrays
- [x] Entender índices
- [x] Utilizar `length`
- [x] Utilizar `split()`

**Validação**
- [x] Validar quantidade de campos
- [x] Validar campos obrigatórios
- [x] Tratar registros inválidos sem interromper a migração

**Transformação**
- [x] Converter ID
- [x] Transformar `ativo`
- [x] Converter datas
- [ ] Normalizar CPF
- [ ] Normalizar telefone
- [ ] Melhorar tratamento de valores opcionais

**Banco de dados**
- [x] Conectar ao MySQL
- [x] Criar `PreparedStatement`
- [x] Definir parâmetros
- [x] Inserir registros
- [x] Conferir registros migrados
- [x] Tratar erros por registro
- [ ] Trabalhar com transações
- [ ] Gerar relatório formal da migração

**Migração dos arquivos**
- [x] clientes.csv
- [ ] usuarios.csv
- [ ] enderecos.csv
- [ ] categorias.csv
- [ ] produtos.csv
- [ ] produto_imagens.csv
- [ ] estoques.csv
- [ ] carrinhos.csv
- [ ] pedidos.csv
- [ ] pedido_itens.csv

**Análise da base legada**
- [ ] Identificar inconsistências nos dados analisados
- [ ] Identificar campos vazios
- [ ] Identificar formatos de dados inconsistentes
- [ ] Identificar registro duplicado
- [ ] Documentar todos os problemas encontrados
- [ ] Definir tratamento para todos os registros problemáticos

**Refatoração**
- [ ] Aplicar POO
- [ ] Separar responsabilidades
- [ ] Criar classes específicas para cada etapa
- [ ] Criar métodos reutilizáveis
- [ ] Adicionar testes
- [ ] Melhorar arquitetura

## Estado atual

A primeira migração foi concluída com sucesso utilizando `clientes.csv`.

Fluxo atual em funcionamento:

```
CSV → Leitura → Parsing → Validação → Transformação → PreparedStatement → MySQL
```

Resultado da primeira execução completa:

- 60 registros lidos
- 59 registros inseridos
- 1 registro rejeitado (email duplicado, tratado sem interromper o processamento)

A estrutura inicial do projeto já foi organizada em pacotes, preparando o código para a evolução posterior.

## Próximos passos

Após a conclusão de `clientes.csv`, o projeto seguirá para os demais arquivos da base. A ordem será definida considerando as dependências entre as tabelas, principalmente as relacionadas por chaves estrangeiras.

Para cada arquivo será seguido o mesmo processo:

```
Analisar → Ler → Validar → Transformar → Persistir → Testar → Documentar
```

Depois que os fluxos individuais estiverem funcionando, será realizada uma refatoração geral para reduzir duplicação e melhorar a organização do projeto.

## Filosofia do projeto

Este projeto está sendo desenvolvido com foco em aprendizado e compreensão, evitando abstrações prematuras. A implementação começa de forma simples para entender o funcionamento de cada etapa antes de introduzir abstrações maiores.

O objetivo é entender não apenas o que fazer, mas por que cada decisão foi tomada.

Evolução planejada:

```
Java básico → JDBC → POO → separação de responsabilidades → reutilização → testes → arquitetura
```

O objetivo final não é apenas ter uma rotina de migração funcional, mas compreender profundamente o processo de migração de uma base legada e as decisões envolvidas em sua implementação.