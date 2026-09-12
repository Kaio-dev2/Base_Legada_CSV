package br.com.loja;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import br.com.loja.conexao.Conexao;
import br.com.loja.migracao.EnderecosMigracao;


public class Main {

    public static void main(String[] args) throws IOException, SQLException {

        Connection conexao = Conexao.conectar();

        System.out.println("Conectado!");

        EnderecosMigracao migracao3 = new EnderecosMigracao();

        System.out.println("=== MIGRANDO ENDERECOS ===");
        migracao3.migrar(conexao);
    }
    }
