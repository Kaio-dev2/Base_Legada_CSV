package br.com.loja;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import br.com.loja.conexao.Conexao;
import br.com.loja.migracao.PedidoItensMigracao;


public class Main {

    public static void main(String[] args) throws IOException, SQLException {

        Connection conexao = Conexao.conectar();

        System.out.println("Conectado!");

        PedidoItensMigracao migracao3 = new PedidoItensMigracao();

        System.out.println("=== MIGRANDO PEDIDOS ===");
        migracao3.migrar(conexao);
    }
    }
