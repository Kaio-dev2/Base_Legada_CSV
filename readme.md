# Migração de Base Legada — Java + JDBC

Projeto de estudo para desenvolver uma rotina de migração de dados de uma base legada em CSV para MySQL, utilizando Java e JDBC.

O foco principal é compreender o processo por baixo das abstrações, praticando leitura de arquivos, parsing, validação, transformação de dados, acesso ao banco e persistência.

![Java](https://img.shields.io/badge/Java%2021-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-007396?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)


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
│                       │   ├── ClientesMigracao.java
│                       │   ├── UsuariosMigracao.java
│                       │   ├── EnderecosMigracao.java
│                       │   ├── CategoriasMigracao.java
│                       │   ├── ProdutosMigracao.java
│                       │   ├── ProdutoImagensMigracao.java
│                       │   ├── EstoquesMigracao.java
│                       │   ├── CarrinhosMigracao.java
│                       │   ├── PedidosMigracao.java
│                       │   └── PedidoItensMigracao.java
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

Antes da persistência, os registros passam por validações básicas.

São verificadas situações como:

- Quantidade correta de campos
- Campos obrigatórios
- Integridade necessária para as conversões
- Erros encontrados durante o processamento
- Integridade referencial
- Restrições de unicidade

Quando um registro apresenta problema, ele é rejeitado sem interromper a migração dos demais.

### 4. Transformação

Os valores do CSV são inicialmente tratados como `String` e depois convertidos para os tipos esperados pela aplicação e pelo banco.

Transformações já implementadas:

- `String` → `int`
- `String` → `boolean`
- `String` → `BigDecimal`
- `String` → `LocalDate`
- `String` → `LocalDateTime`

As representações do campo `ativo` encontradas na base legada são convertidas para `boolean` antes da persistência.

Os valores numéricos de produtos são convertidos para `BigDecimal`, permitindo trabalhar com valores decimais de forma apropriada, principalmente em campos monetários.

Os valores monetários também passam por uma transformação de separador decimal, convertendo valores como:

```
2676,95
```

para:

```
2676.95
```

As datas também são convertidas para `LocalDateTime`, tratando diferentes formatos encontrados durante a análise dos arquivos.

Campos opcionais podem ser transformados em `null` quando não possuem valor no arquivo legado.

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

## Migração de Usuários

Migração realizada com `usuarios.csv → tabela usuarios`. O arquivo possui 8 registros.

Durante a análise foi identificada uma inconsistência de capitalização no campo `tipo`, com valores como `operador` e `OPERADOR`.

A migração realizou:

- Validação da quantidade de campos
- Validação de campos obrigatórios
- Conversão do campo `ativo` para `boolean`
- Conversão das datas para `LocalDateTime`
- Persistência utilizando `PreparedStatement`

Resultado da execução:

```
Total lidas: 8
Inseridas: 8
Rejeitadas: 0
```

A diferença de capitalização do campo `tipo` foi mantida nesta etapa, pois a migração tem como objetivo preservar os dados legados enquanto as transformações necessárias são aplicadas.

## Migração de Endereços

Migração realizada com `enderecos.csv → tabela enderecos`. O arquivo possui 81 registros.

A migração realizou:

- Validação da quantidade de campos
- Validação de campos obrigatórios
- Conversão de `cliente_id` para `int`
- Conversão de `created_at` para `LocalDateTime`
- Validação da integridade referencial com a tabela `clientes`
- Persistência utilizando `PreparedStatement`
- Tratamento individual dos erros, permitindo continuar a migração após uma rejeição

Resultado da execução:

```
Total lidas: 81
Inseridas: 79
Rejeitadas: 2
```

### Registros problemáticos

**ID 54**

O registro possui `cliente_id = 41`. O cliente 41 não foi inserido na nova base devido ao problema de email duplicado identificado durante a migração de `clientes`. Como consequência, o endereço 54 não pôde ser inserido devido à restrição de chave estrangeira entre `enderecos.cliente_id` e `clientes.id`.

**ID 81**

O registro possui `cliente_id = 999`. Não existe um cliente com esse ID na tabela `clientes`, causando uma violação da chave estrangeira durante a tentativa de inserção.

Os dois registros foram rejeitados sem interromper o processamento dos demais registros.

## Migração de Categorias

Migração realizada com `categorias.csv → tabela categorias`. O arquivo possui 19 registros.

A migração realizou:

- Validação da quantidade de campos
- Validação de campos obrigatórios
- Conversão do ID para `int`
- Conversão de `categoria_pai_id`
- Tratamento de categorias sem categoria pai utilizando `NULL`
- Conversão do campo `ativo` para `boolean`
- Conversão das datas para `LocalDateTime`
- Validação da integridade referencial da categoria pai
- Persistência utilizando `PreparedStatement`
- Tratamento individual dos erros

Resultado da execução:

```
Total lidas: 19
Inseridas: 18
Rejeitadas: 1
```

### Registro problemático

**ID 19**

O registro possui `categoria_pai_id = 99`, porém a categoria 99 não existe na tabela `categorias`.

O registro foi rejeitado pela chave estrangeira responsável pela relação entre `categoria_pai_id` e `categorias.id`.

As categorias de ID 1 até 6 foram identificadas como categorias principais, enquanto as categorias de ID 7 até 18 possuem referências válidas para suas respectivas categorias pai.

A rejeição demonstra a importância da integridade referencial durante a migração, impedindo que uma categoria seja inserida apontando para uma categoria inexistente.

## Migração de Produtos

Migração realizada com `produtos.csv → tabela produtos`. O arquivo possui 80 registros e apresenta diferentes tipos de dados, incluindo identificadores, textos, valores monetários, dimensões, status e datas.

A migração realizou:

- Validação da quantidade de campos
- Validação de campos obrigatórios
- Conversão de `id` para `int`
- Conversão de `categoria_id` para `int`
- Conversão de `preco` para `BigDecimal`
- Conversão de `preco_promocional` para `BigDecimal`
- Tratamento de `preco_promocional` vazio utilizando `NULL`
- Conversão de `peso` para `BigDecimal`
- Conversão de `altura` para `BigDecimal`
- Conversão de `largura` para `BigDecimal`
- Conversão de `comprimento` para `BigDecimal`
- Conversão do campo `ativo` para `boolean`
- Conversão das datas para `LocalDateTime`
- Persistência utilizando `PreparedStatement`
- Tratamento individual dos erros

Resultado da execução:

```
Total lidas: 80
Inseridas: 79
Rejeitadas: 1
```

### Tratamento de valores monetários

Durante a análise da base foram identificados valores utilizando vírgula como separador decimal, como:

```
2676,95
5461,06
```

A migração realiza a transformação:

```java
campos[6].replace(",", ".")
```

antes da criação do `BigDecimal`, permitindo transformar a representação textual encontrada no CSV em um valor numérico compatível com a persistência no banco.

### Tratamento de preço promocional

O campo `preco_promocional` pode estar vazio no arquivo legado.

Quando o campo está vazio, a aplicação utiliza `null`:

```java
BigDecimal precoPromocional = campos[7].isEmpty()
        ? null
        : new BigDecimal(campos[7].replace(",", "."));
```

Na persistência, o valor é tratado de acordo com sua existência:

```java
if (precoPromocional == null) {
    stmt.setNull(8, java.sql.Types.DECIMAL);
} else {
    stmt.setBigDecimal(8, precoPromocional);
}
```

Dessa forma, um preço promocional ausente é armazenado como `NULL`, enquanto um preço existente é armazenado como `DECIMAL`.

### Registro problemático

**ID 57**

O registro possui o SKU `SKU-1012`, já utilizado por outro produto da base. A restrição de unicidade do campo `sku` causou a rejeição do registro duplicado durante a persistência.

Durante a análise do arquivo também foram identificadas outras situações relevantes, ainda sem impacto na rejeição de registros: produtos com `preco_promocional` vazio e um produto com `preco_promocional` superior ao `preco`.

## Migração de Produto Imagens

Migração realizada com `produto_imagens.csv → tabela produto_imagens`. O arquivo possui 201 registros.

Resultado da execução:

```
Total lidas: 201
Inseridas: 199
Rejeitadas: 2
```

### Registros problemáticos

**IDs 142 e 143**

Os dois registros possuem `produto_id = 57`. O produto 57 não foi inserido anteriormente devido à duplicidade do SKU `SKU-1012`. Como consequência, as duas imagens não puderam ser inseridas devido à chave estrangeira entre `produto_imagens.produto_id` e `produtos.id`.

Isso demonstra uma dependência entre registros de diferentes arquivos da base legada.

## Migração de Estoques

Migração realizada com `estoques.csv → tabela estoques`. O arquivo possui 78 registros.

Resultado da execução:

```
Total lidas: 78
Inseridas: 77
Rejeitadas: 1
```

### Registro problemático

**ID 56**

O registro possui `produto_id = 57`. Como o produto 57 foi rejeitado anteriormente devido ao SKU duplicado, o estoque correspondente também foi rejeitado pela chave estrangeira.

Durante a análise também foi identificado um estoque com quantidade `-8`, caracterizando uma inconsistência de negócio que foi aceita pelo banco nesta etapa.

## Migração de Carrinhos

Migração realizada com `carrinhos.csv → tabela carrinhos`. O arquivo possui 45 registros.

Resultado da execução:

```
Total lidas: 45
Inseridas: 44
Rejeitadas: 1
```

### Registro problemático

**ID 40**

O registro possui `cliente_id = 777`, porém o cliente 777 não existe na nova base. O registro foi rejeitado pela chave estrangeira.

Também foram observadas diferenças de capitalização no campo `status`, como `aberto`, `ABANDONADO`, `Aberto` e `abandonado`.

## Migração de Pedidos

Migração realizada com `pedidos.csv → tabela pedidos`. O arquivo possui 120 registros.

Resultado da execução:

```
Total lidas: 120
Inseridas: 117
Rejeitadas: 3
```

### Registros problemáticos

- Pedido 20 → `cliente_id = 41`
- Pedido 63 → `cliente_id = 888`
- Pedido 80 → `cliente_id = 41`

O cliente 41 não foi migrado devido ao email duplicado identificado anteriormente. O cliente 888 não existe na base. Como consequência, os pedidos relacionados foram rejeitados pela chave estrangeira.

A migração continuou normalmente após as rejeições.

## Migração de Itens de Pedidos

Migração realizada com `pedido_itens.csv → tabela pedido_itens`. O arquivo possui 301 registros.

Resultado da execução:

```
Total lidas: 301
Inseridas: 289
Rejeitadas: 12
```

Foram identificadas dependências com registros que não foram migrados anteriormente.

Itens relacionados a pedidos inexistentes:

- itens 52, 53, 54 e 55 → pedido 20
- item 159 → pedido 63
- itens 195, 196 e 197 → pedido 80

Itens relacionados a produtos inexistentes:

- item 4 → produto 57
- item 71 → produto 9999
- item 166 → produto 57
- item 245 → produto 57

Os registros foram rejeitados pelas chaves estrangeiras, sem interromper a migração dos demais itens.

## Fluxo atual

```
CSV
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

## Resumo das migrações

| Arquivo | Lidos | Inseridos | Rejeitados |
|---|---:|---:|---:|
| usuarios.csv | 8 | 8 | 0 |
| clientes.csv | 60 | 59 | 1 |
| enderecos.csv | 81 | 79 | 2 |
| categorias.csv | 19 | 18 | 1 |
| produtos.csv | 80 | 79 | 1 |
| produto_imagens.csv | 201 | 199 | 2 |
| estoques.csv | 78 | 77 | 1 |
| carrinhos.csv | 45 | 44 | 1 |
| pedidos.csv | 120 | 117 | 3 |
| pedido_itens.csv | 301 | 289 | 12 |
| **TOTAL** | **993** | **969** | **24** |

### Dependência entre produtos e imagens

Durante a migração de `produto_imagens.csv`, foram lidos 201 registros e 199 foram inseridos.

Os registros de ID 142 e 143 possuem `produto_id = 57`. O produto 57 havia sido rejeitado anteriormente devido à duplicidade do SKU `SKU-1012`.

Como consequência, as duas imagens também foram rejeitadas pela chave estrangeira que relaciona `produto_imagens.produto_id` com `produtos.id`.

Isso é importante porque mostra que os problemas encontrados em uma tabela podem gerar rejeições em tabelas dependentes.

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
- `BigDecimal`, `replace()`
- Conversão e tratamento de datas com `LocalDate` e `LocalDateTime`, `DateTimeFormatter`, `DateTimeParseException`
- Tratamento de `null`

**JDBC**
- `Connection`, `DriverManager`, `PreparedStatement`
- `setInt()`, `setString()`, `setBoolean()`, `setBigDecimal()`, `setNull()`, `setObject()`, `executeUpdate()`

**Banco de dados**
- SQL, `INSERT`, chaves primárias, chaves estrangeiras, restrições de unicidade
- Tipos `DECIMAL`, `NULL`, integridade referencial
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
- [x] Identificar problemas de integridade referencial
- [x] Identificar registros duplicados

**Transformação**
- [x] Converter ID
- [x] Transformar `ativo`
- [x] Converter datas
- [x] Converter valores numéricos para `BigDecimal`
- [x] Tratar valores opcionais com `NULL`
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
- [x] usuarios.csv
- [x] enderecos.csv
- [x] categorias.csv
- [x] produtos.csv
- [x] produto_imagens.csv
- [x] estoques.csv
- [x] carrinhos.csv
- [x] pedidos.csv
- [x] pedido_itens.csv

**Análise da base legada**
- [x] Identificar inconsistências nos dados analisados
- [x] Identificar campos vazios
- [x] Identificar formatos de dados inconsistentes
- [x] Identificar registros duplicados
- [x] Identificar problemas de integridade referencial
- [x] Documentar os problemas encontrados nas tabelas analisadas
- [x] Definir tratamento para os registros problemáticos

**Refatoração**
- [ ] Aplicar POO
- [ ] Separar responsabilidades
- [ ] Criar classes específicas para cada etapa
- [ ] Criar métodos reutilizáveis
- [ ] Adicionar testes
- [ ] Melhorar arquitetura

## Estado atual

Os 10 arquivos da base legada já possuem rotinas de migração implementadas e executadas.

Resultado consolidado:

| Arquivo | Lidos | Inseridos | Rejeitados |
|---|---:|---:|---:|
| usuarios.csv | 8 | 8 | 0 |
| clientes.csv | 60 | 59 | 1 |
| enderecos.csv | 81 | 79 | 2 |
| categorias.csv | 19 | 18 | 1 |
| produtos.csv | 80 | 79 | 1 |
| produto_imagens.csv | 201 | 199 | 2 |
| estoques.csv | 78 | 77 | 1 |
| carrinhos.csv | 45 | 44 | 1 |
| pedidos.csv | 120 | 117 | 3 |
| pedido_itens.csv | 301 | 289 | 12 |
| **TOTAL** | **993** | **969** | **24** |

Principais problemas encontrados:

- email duplicado
- SKU duplicado
- categorias com pai inexistente
- clientes inexistentes
- produtos inexistentes
- registros dependentes de entidades rejeitadas
- valores monetários com diferentes separadores decimais
- preço promocional superior ao preço normal
- quantidade de estoque negativa
- diferenças de capitalização em campos de status/tipo

O fluxo utilizado em todas as migrações permanece:

```
CSV → Leitura → Parsing → Validação → Transformação → PreparedStatement → MySQL
```

A estrutura do projeto já está organizada em pacotes, permitindo continuar a implementação das próximas etapas.

## Próximos passos

Com as migrações dos 10 arquivos concluídas, o próximo fluxo passa a ser:

```
Validar migração completa → Analisar inconsistências → Organizar documentação → Refatorar → Adicionar testes → Gerar relatório final
```

A refatoração não será feita às cegas: primeiro a migração é fechada como está e o README é commitado, para só então avançar para a etapa de validação e refatoração.

## Filosofia do projeto

Este projeto está sendo desenvolvido com foco em aprendizado e compreensão, evitando abstrações prematuras. A implementação começa de forma simples para entender o funcionamento de cada etapa antes de introduzir abstrações maiores.

O objetivo é entender não apenas o que fazer, mas por que cada decisão foi tomada.

Evolução planejada:

```
Java básico → JDBC → POO → separação de responsabilidades → reutilização → testes → arquitetura
```

O objetivo final não é apenas ter uma rotina de migração funcional, mas compreender profundamente o processo de migração de uma base legada e as decisões envolvidas em sua implementação.
