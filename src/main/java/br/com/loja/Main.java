package br.com.loja;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import br.com.loja.conexao.Conexao;
import br.com.loja.migracao.CarrinhosMigracao;
import br.com.loja.migracao.CategoriasMigracao;
import br.com.loja.migracao.ClientesMigracao;
import br.com.loja.migracao.EnderecosMigracao;
import br.com.loja.migracao.EstoquesMigracao;
import br.com.loja.migracao.PedidoItensMigracao;
import br.com.loja.migracao.PedidosMigracao;
import br.com.loja.migracao.ProdutoImagensMigracao;
import br.com.loja.migracao.ProdutosMigracao;
import br.com.loja.migracao.UsuariosMigracao;

public class Main {

    public static void main(String[] args) throws IOException, SQLException {

        Connection conexao = Conexao.conectar();

        System.out.println("Conectado!");

        
        UsuariosMigracao migracao1 = new UsuariosMigracao();
        ClientesMigracao migracao2 = new ClientesMigracao();
        EnderecosMigracao migracao3 = new EnderecosMigracao();
        CategoriasMigracao migracao4 = new CategoriasMigracao();
        ProdutosMigracao migracao5 = new ProdutosMigracao();
        ProdutoImagensMigracao migracao6 = new ProdutoImagensMigracao();
        EstoquesMigracao migracao7 = new EstoquesMigracao();
        CarrinhosMigracao migracao8 = new CarrinhosMigracao();
        PedidosMigracao migracao9 = new PedidosMigracao();
        PedidoItensMigracao migracao10 = new PedidoItensMigracao();


        System.out.println("=== MIGRANDO DADOS ===");
        migracao1.migrar(conexao);
        migracao2.migrar(conexao);
        migracao3.migrar(conexao);
        migracao4.migrar(conexao);
        migracao5.migrar(conexao);
        migracao6.migrar(conexao);
        migracao7.migrar(conexao);
        migracao8.migrar(conexao);
        migracao9.migrar(conexao);
        migracao10.migrar(conexao);
    }
    }
