import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;//para conexão com o banco de dados
import java.sql.DriverManager;//para conexão com o banco de dados
import java.sql.SQLException;//para conexão com o banco de dados
import java.time.format.DateTimeFormatter;
public class Main{
    public static void main(String[] args) throws IOException, SQLException{


        
        // Conexão com o banco de dados
        String url ="jdbc:mysql://localhost:3306/loja_virtual";
        String usuario = "root";
        String senha = "123456";

        Connection conexao = DriverManager.getConnection(url, usuario, senha);

        System.out.println("Conectado!");

        // Leitura do arquivo CSV
        Path caminho = Path.of("clientes.csv");
        BufferedReader leitor = Files.newBufferedReader(caminho);

        
        
        String linha = leitor.readLine(); // vai consumir a primeira linha do arquivo, que é o cabeçalho, e não será processada
        while((linha = leitor.readLine())!= null){ // aqui começa a leitura do arquivo, linha por linha, até o final do arquivo
            String[] campos = linha.split(";");
            if(campos.length == 9 && !campos[1].isEmpty() && !campos[2].isEmpty() && !campos[5].isEmpty()){
                System.out.println("Linha valida:" + linha);
                
                int id = Integer.parseInt(campos[0]);
                System.out.println("ID: " + id);

                String textoAtivo = campos[6];
                System.out.println("Texto Ativo: " + textoAtivo);

                // aqui uma demonstração lógica usando if para determinar o que o csv retorna: 1 = True,0 = False,S = True,N = False
                boolean ativo = textoAtivo.equals("1") || textoAtivo.equals("S");
                System.out.println("Ativo: " + textoAtivo);
                System.out.println("Ativo: " + ativo);

                String dataCriacao = campos[7];
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // um formatador de data e hora para o padrão do MySQL
                DateTimeFormatter formatoBrasileiro = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                if (dataCriacao.contains("-") && dataCriacao.contains(" ")){
                    System.out.println("formato ISO Data e Hora");

                } else if (dataCriacao.contains("/") && dataCriacao.contains(" ")){
                    System.out.println("formato brasileiro Data e Hora");

                }else if (dataCriacao.contains("/")){
                    System.out.println("formato brasileiro só Data");

                }
                
                
            }
            
        }
    }
    }
