package br.com.loja;

import br.com.loja.conexao.Conexao;
import br.com.loja.migracao.ClientesMigracao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException, SQLException {

        Connection conexao = Conexao.conectar();

        System.out.println("Conectado!");

        ClientesMigracao migracao = new ClientesMigracao();

        migracao.migrar(conexao);
    }
}