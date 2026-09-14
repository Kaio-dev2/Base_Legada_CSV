package br.com.loja;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import br.com.loja.conexao.Conexao;
import br.com.loja.migracao.CategoriasMigracao;


public class Main {

    public static void main(String[] args) throws IOException, SQLException {

        Connection conexao = Conexao.conectar();

        System.out.println("Conectado!");

        CategoriasMigracao migracao3 = new CategoriasMigracao();

        System.out.println("=== MIGRANDO CATEGORIAS ===");
        migracao3.migrar(conexao);
    }
    }
