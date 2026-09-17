package br.com.loja;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import br.com.loja.conexao.Conexao;
import br.com.loja.migracao.ProdutoImagensMigracao;


public class Main {

    public static void main(String[] args) throws IOException, SQLException {

        Connection conexao = Conexao.conectar();

        System.out.println("Conectado!");

        ProdutoImagensMigracao migracao3 = new ProdutoImagensMigracao();

        System.out.println("=== MIGRANDO PRODUTOS IMAGENS ===");
        migracao3.migrar(conexao);
    }
    }
