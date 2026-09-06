import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main{
    public static void main(String[] args) throws IOException{



        // Conexão com o banco de dados
        String url ="jdbc:mysql://localhost:3306/loja_virtual";
        String usuario = "root";
        String senha = "123456";

        
        System.out.println("Conectado!");

        // Leitura do arquivo CSV
        Path caminho = Path.of(("../base_legada/clientes.csv"));
        BufferedReader leitor = Files.newBufferedReader(caminho);

        
        
        String linha;
        while((linha = leitor.readLine())!= null){
            String[] campos = linha.split(";");
            if(campos.length == 9 && !campos[1].isEmpty() && !campos[2].isEmpty() && !campos[5].isEmpty()){
                System.out.println("Linha valida:" + linha);
                
                int numero = Integer.parseInt(campos[6]);
                System.out.println("Numero: " + numero);
            }
        }
    }
    }
