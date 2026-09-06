-- ============================================================
-- BANCO NOVO - LOJA VIRTUAL
-- Compatível com MySQL 8.x / MySQL Workbench
-- Origem: base_legada (CSV)
-- ============================================================

DROP DATABASE IF EXISTS loja_virtual;
CREATE DATABASE loja_virtual
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE loja_virtual;

-- ============================================================
-- 1. USUÁRIOS INTERNOS
-- ============================================================
CREATE TABLE usuarios (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_usuarios_email (email),
    INDEX idx_usuarios_tipo (tipo)
) ENGINE=InnoDB;

-- ============================================================
-- 2. CLIENTES
-- ============================================================
CREATE TABLE clientes (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL,
    cpf VARCHAR(14) NULL,
    telefone VARCHAR(20) NULL,
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_clientes_email (email),
    INDEX idx_clientes_cpf (cpf)
) ENGINE=InnoDB;

-- ============================================================
-- 3. ENDEREÇOS
-- ============================================================
CREATE TABLE enderecos (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    cliente_id INT UNSIGNED NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    cep VARCHAR(9) NOT NULL,
    logradouro VARCHAR(150) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    complemento VARCHAR(100) NULL,
    bairro VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado CHAR(2) NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_enderecos_cliente (cliente_id),
    CONSTRAINT fk_enderecos_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 4. CATEGORIAS
-- categoria_pai_id permite categorias hierárquicas
-- ============================================================
CREATE TABLE categorias (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL,
    descricao TEXT NULL,
    imagem VARCHAR(255) NULL,
    categoria_pai_id INT UNSIGNED NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_categorias_pai (categoria_pai_id),
    INDEX idx_categorias_slug (slug),
    CONSTRAINT fk_categorias_pai
        FOREIGN KEY (categoria_pai_id)
        REFERENCES categorias(id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- 5. PRODUTOS
-- ============================================================
CREATE TABLE produtos (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    categoria_id INT UNSIGNED NOT NULL,
    nome VARCHAR(150) NOT NULL,
    slug VARCHAR(180) NOT NULL,
    descricao TEXT NOT NULL,
    sku VARCHAR(50) NOT NULL,
    preco DECIMAL(12,2) NOT NULL,
    preco_promocional DECIMAL(12,2) NULL,
    peso DECIMAL(10,3) NOT NULL,
    altura DECIMAL(10,2) NOT NULL,
    largura DECIMAL(10,2) NOT NULL,
    comprimento DECIMAL(10,2) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_produtos_categoria (categoria_id),
    INDEX idx_produtos_sku (sku),
    INDEX idx_produtos_slug (slug),
    CONSTRAINT fk_produtos_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categorias(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT chk_produtos_preco
        CHECK (preco >= 0),
    CONSTRAINT chk_produtos_preco_promocional
        CHECK (preco_promocional IS NULL OR preco_promocional >= 0),
    CONSTRAINT chk_produtos_peso
        CHECK (peso >= 0)
) ENGINE=InnoDB;

-- ============================================================
-- 6. IMAGENS DOS PRODUTOS
-- ============================================================
CREATE TABLE produto_imagens (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    produto_id INT UNSIGNED NOT NULL,
    url VARCHAR(500) NOT NULL,
    ordem INT UNSIGNED NOT NULL DEFAULT 1,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_produto_imagens_produto (produto_id),
    CONSTRAINT fk_produto_imagens_produto
        FOREIGN KEY (produto_id)
        REFERENCES produtos(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 7. ESTOQUE
-- Relação 1:1 com produto
-- ============================================================
CREATE TABLE estoques (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    produto_id INT UNSIGNED NOT NULL,
    quantidade INT NOT NULL DEFAULT 0,
    quantidade_minima INT NOT NULL DEFAULT 0,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_estoques_produto (produto_id),
    CONSTRAINT fk_estoques_produto
        FOREIGN KEY (produto_id)
        REFERENCES produtos(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT chk_estoques_quantidade
        CHECK (quantidade >= 0),
    CONSTRAINT chk_estoques_quantidade_minima
        CHECK (quantidade_minima >= 0)
) ENGINE=InnoDB;

-- ============================================================
-- 8. CARRINHOS
-- ============================================================
CREATE TABLE carrinhos (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    cliente_id INT UNSIGNED NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_carrinhos_cliente (cliente_id),
    INDEX idx_carrinhos_status (status),
    CONSTRAINT fk_carrinhos_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ============================================================
-- 9. PEDIDOS
-- ============================================================
CREATE TABLE pedidos (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    cliente_id INT UNSIGNED NOT NULL,
    endereco_id INT UNSIGNED NOT NULL,
    status VARCHAR(40) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    desconto DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    frete DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    forma_pagamento VARCHAR(40) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_pedidos_cliente (cliente_id),
    INDEX idx_pedidos_endereco (endereco_id),
    INDEX idx_pedidos_status (status),
    INDEX idx_pedidos_created_at (created_at),
    CONSTRAINT fk_pedidos_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_pedidos_endereco
        FOREIGN KEY (endereco_id)
        REFERENCES enderecos(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT chk_pedidos_valores
        CHECK (
            subtotal >= 0 AND
            desconto >= 0 AND
            frete >= 0 AND
            total >= 0
        )
) ENGINE=InnoDB;

-- ============================================================
-- 10. ITENS DOS PEDIDOS
-- ============================================================
CREATE TABLE pedido_itens (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    pedido_id INT UNSIGNED NOT NULL,
    produto_id INT UNSIGNED NOT NULL,
    quantidade INT UNSIGNED NOT NULL,
    preco_unitario DECIMAL(12,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_pedido_itens_pedido (pedido_id),
    INDEX idx_pedido_itens_produto (produto_id),
    CONSTRAINT fk_pedido_itens_pedido
        FOREIGN KEY (pedido_id)
        REFERENCES pedidos(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_pedido_itens_produto
        FOREIGN KEY (produto_id)
        REFERENCES produtos(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT chk_pedido_itens_quantidade
        CHECK (quantidade > 0),
    CONSTRAINT chk_pedido_itens_preco
        CHECK (preco_unitario >= 0),
    CONSTRAINT chk_pedido_itens_subtotal
        CHECK (subtotal >= 0)
) ENGINE=InnoDB;

-- ============================================================
-- ÍNDICES EXTRAS
-- ============================================================
CREATE INDEX idx_produto_imagens_principal
    ON produto_imagens (produto_id, principal);

CREATE INDEX idx_clientes_ativo
    ON clientes (ativo);

CREATE INDEX idx_produtos_ativo
    ON produtos (ativo);

-- ============================================================
-- FIM
-- ============================================================
