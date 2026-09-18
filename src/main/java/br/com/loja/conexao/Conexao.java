package br.com.loja.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    public static Connection conectar() throws SQLException{

        String url = "jdbc:mysql://localhost:3306/loja_virtual";
        String usuario = "root";
        String senha = "123456";
        return DriverManager.getConnection(url, usuario, senha);
    }
}