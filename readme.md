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

```text
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
│                       │   └── Produtos.java
│                       └── util/
│
├── pom.xml
└── README.md

A estrutura separa a aplicação principal, conexão com o banco, rotinas de migração e futuras classes utilitárias.

Dados da base legada

A base legada é composta por diferentes arquivos CSV relacionados ao funcionamento de uma loja virtual:

usuarios.csv
clientes.csv
enderecos.csv
categorias.csv
produtos.csv
produto_imagens.csv
estoques.csv
carrinhos.csv
pedidos.csv
pedido_itens.csv

Os arquivos utilizam ; como separador e possuem uma linha de cabeçalho.

A análise da base é feita progressivamente, identificando:

Estrutura dos registros
Tipos de dados
Campos obrigatórios e opcionais
Formatos inconsistentes
Registros duplicados
Dependências entre tabelas
Problemas a tratar durante a migração
Processo de migração
1. Leitura

Utilização de Path, Files, BufferedReader e readLine() para acessar o arquivo e percorrê-lo linha por linha. O cabeçalho é lido separadamente antes do processamento dos registros.

2. Parsing

Cada linha do CSV é separada em campos e armazenada em um String[].

Práticas envolvidas:

Arrays
Índices
length
String
split()
Percorrimento de registros

A leitura considera ; como separador.

3. Validação

Antes da persistência, os registros passam por validações básicas.

São verificadas situações como:

Quantidade correta de campos
Campos obrigatórios
Integridade necessária para as conversões
Erros encontrados durante o processamento
Integridade referencial
Restrições de unicidade

Quando um registro apresenta problema, ele é rejeitado sem interromper a migração dos demais.

4. Transformação

Os valores do CSV são inicialmente tratados como String e depois convertidos para os tipos esperados pela aplicação e pelo banco.

Transformações já implementadas:

String → int
String → boolean
String → BigDecimal
String → LocalDate
String → LocalDateTime

As representações do campo ativo encontradas na base legada são convertidas para boolean antes da persistência.

Os valores numéricos de produtos são convertidos para BigDecimal, permitindo trabalhar com valores decimais de forma apropriada, principalmente em campos monetários.

Os valores monetários também passam por uma transformação de separador decimal, convertendo valores como:

2676,95

para:

2676.95

As datas também são convertidas para LocalDateTime, tratando diferentes formatos encontrados durante a análise dos arquivos.

Campos opcionais podem ser transformados em null quando não possuem valor no arquivo legado.

5. Persistência

Realizada via JDBC:

Abrir conexão com o MySQL
Criar um PreparedStatement
Definir os parâmetros
Executar o INSERT
Tratar possíveis erros
Continuar o processamento dos próximos registros
Conferir os dados persistidos no banco

O PreparedStatement permite trabalhar com parâmetros separados da instrução SQL, evitando a construção direta dos valores na query.

Primeira migração — Clientes

Migração completa realizada com clientes.csv → tabela clientes. O arquivo possui 60 registros.

Resultado da execução:

Total lidas: 60
Inseridas: 59
Rejeitadas: 1
Registro problemático

Foi identificado um registro com email duplicado. O registro de ID 41 foi rejeitado devido à restrição de unicidade do campo email na tabela clientes.

O banco retornou:

Duplicate entry 'juliana.prado@email.com'

O erro não interrompeu a migração — os registros seguintes continuaram sendo processados normalmente até o ID 60, demonstrando o tratamento de erros por registro implementado na rotina.

Migração de Usuários

Migração realizada com usuarios.csv → tabela usuarios. O arquivo possui 8 registros.

Durante a análise foi identificada uma inconsistência de capitalização no campo tipo, com valores como operador e OPERADOR.

A migração realizou:

Validação da quantidade de campos
Validação de campos obrigatórios
Conversão do campo ativo para boolean
Conversão das datas para LocalDateTime
Persistência utilizando PreparedStatement

Resultado da execução:

Total lidas: 8
Inseridas: 8
Rejeitadas: 0

A diferença de capitalização do campo tipo foi mantida nesta etapa, pois a migração tem como objetivo preservar os dados legados enquanto as transformações necessárias são aplicadas.

Migração de Endereços

Migração realizada com enderecos.csv → tabela enderecos. O arquivo possui 81 registros.

A migração realizou:

Validação da quantidade de campos
Validação de campos obrigatórios
Conversão de cliente_id para int
Conversão de created_at para LocalDateTime
Validação da integridade referencial com a tabela clientes
Persistência utilizando PreparedStatement
Tratamento individual dos erros, permitindo continuar a migração após uma rejeição

Resultado da execução:

Total lidas: 81
Inseridas: 79
Rejeitadas: 2
Registros problemáticos

ID 54

O registro possui cliente_id = 41. O cliente 41 não foi inserido na nova base devido ao problema de email duplicado identificado durante a migração de clientes. Como consequência, o endereço 54 não pôde ser inserido devido à restrição de chave estrangeira entre enderecos.cliente_id e clientes.id.

ID 81

O registro possui cliente_id = 999. Não existe um cliente com esse ID na tabela clientes, causando uma violação da chave estrangeira durante a tentativa de inserção.

Os dois registros foram rejeitados sem interromper o processamento dos demais registros.

Migração de Categorias

Migração realizada com categorias.csv → tabela categorias. O arquivo possui 19 registros.

A migração realizou:

Validação da quantidade de campos
Validação de campos obrigatórios
Conversão do ID para int
Conversão de categoria_pai_id
Tratamento de categorias sem categoria pai utilizando NULL
Conversão do campo ativo para boolean
Conversão das datas para LocalDateTime
Validação da integridade referencial da categoria pai
Persistência utilizando PreparedStatement
Tratamento individual dos erros

Resultado da execução:

Total lidas: 19
Inseridas: 18
Rejeitadas: 1
Registro problemático

ID 19

O registro possui categoria_pai_id = 99, porém a categoria 99 não existe na tabela categorias.

O registro foi rejeitado pela chave estrangeira responsável pela relação entre categoria_pai_id e categorias.id.

As categorias de ID 1 até 6 foram identificadas como categorias principais, enquanto as categorias de ID 7 até 18 possuem referências válidas para suas respectivas categorias pai.

A rejeição demonstra a importância da integridade referencial durante a migração, impedindo que uma categoria seja inserida apontando para uma categoria inexistente.

Migração de Produtos

A implementação da migração de produtos.csv → tabela produtos foi iniciada considerando a estrutura de 15 campos do arquivo.

O arquivo possui 80 registros e apresenta diferentes tipos de dados, incluindo identificadores, textos, valores monetários, dimensões, status e datas.

A migração foi estruturada para realizar:

Validação da quantidade de campos
Validação de campos obrigatórios
Conversão de id para int
Conversão de categoria_id para int
Conversão de preco para BigDecimal
Conversão de preco_promocional para BigDecimal
Tratamento de preco_promocional vazio utilizando NULL
Conversão de peso para BigDecimal
Conversão de altura para BigDecimal
Conversão de largura para BigDecimal
Conversão de comprimento para BigDecimal
Conversão do campo ativo para boolean
Conversão das datas para LocalDateTime
Persistência utilizando PreparedStatement
Tratamento individual dos erros
Tratamento de valores monetários

Durante a análise da base foram identificados valores utilizando vírgula como separador decimal, como:

2676,95
5461,06

A migração realiza a transformação:

campos[6].replace(",", ".")

antes da criação do BigDecimal.

Isso permite transformar a representação textual encontrada no CSV em um valor numérico compatível com a persistência no banco.

Tratamento de preço promocional

O campo preco_promocional pode estar vazio no arquivo legado.

Quando o campo está vazio, a aplicação utiliza null:

BigDecimal precoPromocional = campos[7].isEmpty()
        ? null
        : new BigDecimal(campos[7].replace(",", "."));

Na persistência, o valor é tratado de acordo com sua existência:

if (precoPromocional == null) {
    stmt.setNull(8, java.sql.Types.DECIMAL);
} else {
    stmt.setBigDecimal(8, precoPromocional);
}

Dessa forma, um preço promocional ausente é armazenado como NULL, enquanto um preço existente é armazenado como DECIMAL.

Inconsistências identificadas

Durante a análise do arquivo foram identificadas situações que deverão ser verificadas durante a execução da migração:

Valores monetários utilizando vírgula como separador decimal
Produtos com preco_promocional vazio
Produto com preco_promocional superior ao preco
SKU duplicado entre produtos

Um dos SKUs identificados como duplicado é:

SKU-1012

A existência de uma restrição de unicidade no campo sku poderá causar a rejeição do registro duplicado durante a persistência.

O resultado final da migração de produtos será documentado após a execução e análise dos registros rejeitados.

Fluxo atual
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
Resumo das migrações concluídas

Até o momento, quatro arquivos já foram executados:

===== RESUMO DAS MIGRAÇÕES CONCLUÍDAS =====

Total lidas: 168
Inseridas: 164
Rejeitadas: 4

Detalhamento:

clientes.csv    → 60 lidos, 59 inseridos, 1 rejeitado
usuarios.csv    → 8 lidos, 8 inseridos, 0 rejeitados
enderecos.csv   → 81 lidos, 79 inseridos, 2 rejeitados
categorias.csv  → 19 lidos, 18 inseridos, 1 rejeitado

A migração de produtos.csv possui a implementação preparada, mas seu resultado de execução ainda será registrado após o teste.

Conceitos estudados

Manipulação de arquivos

Path, Files, BufferedReader, readLine()

Estruturas da linguagem

Arrays
String
Índices
length
Loops
Condicionais
Métodos

Validação e tratamento

Validação de quantidade de campos e campos obrigatórios
try/catch
SQLException
RuntimeException
Tratamento de erro por registro

Conversão de dados

Integer.parseInt()
Conversão para boolean
BigDecimal
replace()
Conversão e tratamento de datas com LocalDate e LocalDateTime
DateTimeFormatter
DateTimeParseException
Tratamento de null

JDBC

Connection
DriverManager
PreparedStatement
setInt()
setString()
setBoolean()
setBigDecimal()
setNull()
setObject()
executeUpdate()

Banco de dados

SQL
INSERT
Chaves primárias
Chaves estrangeiras
Restrições de unicidade
Tipos DECIMAL
NULL
Integridade referencial
Persistência e validação dos registros após a migração
Progresso

Leitura

 Localizar arquivo
 Abrir arquivo
 Ler linha por linha
 Ignorar cabeçalho

Parsing

 Separar campos
 Trabalhar com arrays
 Entender índices
 Utilizar length
 Utilizar split()

Validação

 Validar quantidade de campos
 Validar campos obrigatórios
 Tratar registros inválidos sem interromper a migração
 Identificar problemas de integridade referencial
 Identificar registros duplicados

Transformação

 Converter ID
 Transformar ativo
 Converter datas
 Converter valores numéricos para BigDecimal
 Tratar valores opcionais com NULL
 Normalizar CPF
 Normalizar telefone
 Melhorar tratamento de valores opcionais

Banco de dados

 Conectar ao MySQL
 Criar PreparedStatement
 Definir parâmetros
 Inserir registros
 Conferir registros migrados
 Tratar erros por registro
 Trabalhar com transações
 Gerar relatório formal da migração

Migração dos arquivos

 clientes.csv
 usuarios.csv
 enderecos.csv
 categorias.csv
 produtos.csv
 produto_imagens.csv
 estoques.csv
 carrinhos.csv
 pedidos.csv
 pedido_itens.csv

Análise da base legada

 Identificar inconsistências nos dados analisados
 Identificar campos vazios
 Identificar formatos de dados inconsistentes
 Identificar registros duplicados
 Identificar problemas de integridade referencial
 Documentar os problemas encontrados nas tabelas analisadas
 Definir tratamento para os registros problemáticos

Refatoração

 Aplicar POO
 Separar responsabilidades
 Criar classes específicas para cada etapa
 Criar métodos reutilizáveis
 Adicionar testes
 Melhorar arquitetura
Estado atual

Quatro arquivos da base legada já foram migrados:

clientes.csv: 60 lidos, 59 inseridos e 1 rejeitado
usuarios.csv: 8 lidos, 8 inseridos e 0 rejeitados
enderecos.csv: 81 lidos, 79 inseridos e 2 rejeitados
categorias.csv: 19 lidos, 18 inseridos e 1 rejeitado

A implementação da migração de produtos.csv já foi criada, incluindo tratamento de valores numéricos com BigDecimal e tratamento de valores opcionais com NULL.

Os registros rejeitados são tratados individualmente, permitindo que os demais registros continuem sendo processados.

Também foram identificadas dependências entre os dados. Um dos endereços rejeitados depende de um cliente que já havia sido rejeitado em uma etapa anterior, enquanto outro referencia um cliente inexistente na base.

Na migração de produtos também foram identificadas possíveis inconsistências de dados, incluindo valores monetários com diferentes separadores decimais, preço promocional superior ao preço normal e SKU duplicado.

O fluxo atual permanece:

CSV → Leitura → Parsing → Validação → Transformação → PreparedStatement → MySQL

A estrutura do projeto já está organizada em pacotes, permitindo continuar a implementação dos demais arquivos antes da etapa de refatoração.

Próximos passos

Após a conclusão das primeiras migrações, o projeto seguirá para os demais arquivos da base. A ordem será definida considerando as dependências entre as tabelas, principalmente as relacionadas por chaves estrangeiras.

Para cada arquivo será seguido o mesmo processo:

Analisar → Ler → Validar → Transformar → Persistir → Testar → Documentar

Próximo passo imediato:

Executar produtos.csv
        ↓
Analisar registros rejeitados
        ↓
Registrar resultado no README
        ↓
Commit da documentação

Depois que os fluxos individuais estiverem funcionando, será realizada uma refatoração geral para reduzir duplicação e melhorar a organização do projeto.

Filosofia do projeto

Este projeto está sendo desenvolvido com foco em aprendizado e compreensão, evitando abstrações prematuras. A implementação começa de forma simples para entender o funcionamento de cada etapa antes de introduzir abstrações maiores.

O objetivo é entender não apenas o que fazer, mas por que cada decisão foi tomada.

Evolução planejada:

Java básico → JDBC → POO → separação de responsabilidades → reutilização → testes → arquitetura

O objetivo final não é apenas ter uma rotina de migração funcional, mas compreender profundamente o processo de migração de uma base legada e as decisões envolvidas em sua implementação.