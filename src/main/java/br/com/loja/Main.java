package br.com.loja;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import br.com.loja.conexao.Conexao;
import br.com.loja.migracao.ClientesMigracao;
import br.com.loja.migracao.UsuariosMigracao;


public class Main {

    public static void main(String[] args) throws IOException, SQLException {

        Connection conexao = Conexao.conectar();

        System.out.println("Conectado!");

        ClientesMigracao migracao1 = new ClientesMigracao();
        UsuariosMigracao migracao2 = new UsuariosMigracao();


        System.out.println("=== MIGRANDO CLIENTES ===");
        migracao1.migrar(conexao);

        System.out.println();
        System.out.println("=== MIGRANDO USUARIOS ===");
        migracao2.migrar(conexao);
    }
    }
