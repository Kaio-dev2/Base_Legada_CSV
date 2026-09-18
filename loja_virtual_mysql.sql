DROP DATABASE IF EXISTS loja_virtual;

CREATE DATABASE loja_virtual
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE loja_virtual;


-- =========================================================
-- 1. USUÁRIOS
-- =========================================================

CREATE TABLE usuarios (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100),
    email VARCHAR(150),
    senha VARCHAR(255),
    tipo VARCHAR(30),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,

    PRIMARY KEY (id),

    UNIQUE KEY uk_usuarios_email (email)
) ENGINE=InnoDB;


-- =========================================================
-- 2. CLIENTES
-- =========================================================

CREATE TABLE clientes (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL,
    cpf VARCHAR(20),
    telefone VARCHAR(20),
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,

    PRIMARY KEY (id),

    UNIQUE KEY uk_clientes_email (email)
) ENGINE=InnoDB;


-- =========================================================
-- 3. CATEGORIAS
-- =========================================================

CREATE TABLE categorias (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    slug VARCHAR(255),
    descricao TEXT,
    imagem VARCHAR(255),
    categoria_pai_id INT UNSIGNED,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,

    PRIMARY KEY (id),

    INDEX idx_categorias_pai (categoria_pai_id),

    CONSTRAINT fk_categoria_pai
        FOREIGN KEY (categoria_pai_id)
        REFERENCES categorias(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;


-- =========================================================
-- 4. ENDEREÇOS
-- =========================================================

CREATE TABLE enderecos (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    cliente_id INT UNSIGNED,
    tipo VARCHAR(50),
    cep VARCHAR(10),
    logradouro VARCHAR(150),
    numero VARCHAR(20),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    estado VARCHAR(2),
    created_at DATETIME,

    PRIMARY KEY (id),

    INDEX idx_enderecos_cliente (cliente_id),

    CONSTRAINT fk_endereco_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;


-- =========================================================
-- 5. PRODUTOS
-- =========================================================

CREATE TABLE produtos (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    categoria_id INT UNSIGNED,
    nome VARCHAR(150) NOT NULL,
    slug VARCHAR(255),
    descricao TEXT,
    sku VARCHAR(50),
    preco DECIMAL(10,2) DEFAULT 0.00,
    preco_promocional DECIMAL(10,2),
    peso INT,
    altura INT,
    largura INT,
    comprimento INT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,

    PRIMARY KEY (id),

    UNIQUE KEY uk_produtos_sku (sku),

    INDEX idx_produtos_categoria (categoria_id),

    CONSTRAINT fk_produto_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categorias(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;


-- =========================================================
-- 6. IMAGENS DOS PRODUTOS
-- =========================================================

CREATE TABLE produto_imagens (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    produto_id INT UNSIGNED,
    url VARCHAR(500),
    ordem INT DEFAULT 1,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME,

    PRIMARY KEY (id),

    INDEX idx_produto_imagens_produto (produto_id),

    CONSTRAINT fk_imagem_produto
        FOREIGN KEY (produto_id)
        REFERENCES produtos(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;


-- =========================================================
-- 7. ESTOQUES
-- =========================================================

CREATE TABLE estoques (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    produto_id INT UNSIGNED,
    quantidade INT DEFAULT 0,
    quantidade_minima INT DEFAULT 0,
    updated_at DATETIME,

    PRIMARY KEY (id),

    UNIQUE KEY uk_estoque_produto (produto_id),

    CONSTRAINT fk_estoque_produto
        FOREIGN KEY (produto_id)
        REFERENCES produtos(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;


-- =========================================================
-- 8. CARRINHOS
-- =========================================================

CREATE TABLE carrinhos (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    cliente_id INT UNSIGNED,
    status VARCHAR(30),
    created_at DATETIME,
    updated_at DATETIME,

    PRIMARY KEY (id),

    INDEX idx_carrinhos_cliente (cliente_id),

    CONSTRAINT fk_carrinho_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;


-- =========================================================
-- 9. PEDIDOS
-- =========================================================

CREATE TABLE pedidos (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    cliente_id INT UNSIGNED,
    endereco_id INT UNSIGNED,
    status VARCHAR(40),
    subtotal DECIMAL(10,2) DEFAULT 0.00,
    desconto DECIMAL(10,2) DEFAULT 0.00,
    frete DECIMAL(10,2) DEFAULT 0.00,
    total DECIMAL(10,2) DEFAULT 0.00,
    forma_pagamento VARCHAR(40),
    created_at DATETIME,
    updated_at DATETIME,

    PRIMARY KEY (id),

    INDEX idx_pedidos_cliente (cliente_id),
    INDEX idx_pedidos_endereco (endereco_id),

    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT fk_pedido_endereco
        FOREIGN KEY (endereco_id)
        REFERENCES enderecos(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;


-- =========================================================
-- 10. ITENS DOS PEDIDOS
-- =========================================================

CREATE TABLE pedido_itens (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    pedido_id INT UNSIGNED,
    produto_id INT UNSIGNED,
    quantidade INT DEFAULT 1,
    preco_unitario DECIMAL(10,2) DEFAULT 0.00,
    subtotal DECIMAL(10,2) DEFAULT 0.00,
    created_at DATETIME,

    PRIMARY KEY (id),

    INDEX idx_pedido_itens_pedido (pedido_id),
    INDEX idx_pedido_itens_produto (produto_id),

    CONSTRAINT fk_item_pedido
        FOREIGN KEY (pedido_id)
        REFERENCES pedidos(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_item_produto
        FOREIGN KEY (produto_id)
        REFERENCES produtos(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB;


-- =========================================================
-- VERIFICAÇÃO
-- =========================================================

SHOW TABLES;