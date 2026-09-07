import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;//para conexão com o banco de dados
import java.sql.DriverManager;//para conexão com o banco de dados
import java.sql.PreparedStatement;//para conexão com o banco de dados
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class Main{
    public static void main(String[] args) throws IOException, SQLException{


        
        // Conexão com o banco de dados
        String url ="jdbc:mysql://localhost:3306/loja_virtual";
        String usuario = "root";
        String senhaBanco = "123456";

        String sql = """
        INSERT INTO clientes
        (id, nome, email, cpf, telefone, senha, ativo, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        Connection conexao = DriverManager.getConnection(url, usuario, senhaBanco);
        PreparedStatement stmt = conexao.prepareStatement(sql);
        
        

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
                String nome = campos[1];
                String email = campos[2];
                String cpf = campos[3];
                String telefone = campos[4];
                String senha = campos[5];

                // transformação do ativo...
                System.out.println("ID: " + id);

                String textoAtivo = campos[6];
                System.out.println("Texto Ativo: " + textoAtivo);

                // aqui uma demonstração lógica usando if para determinar o que o csv retorna: 1 = True,0 = False,S = True,N = False
                boolean ativo = textoAtivo.equals("1") || textoAtivo.equals("S");

                stmt.setInt(1, id);
                stmt.setString(2, nome);
                stmt.setString(3, email);
                stmt.setString(4, cpf);
                stmt.setString(5, telefone);
                stmt.setString(6, senha);
                stmt.setBoolean(7, ativo);

                System.out.println("Ativo: " + textoAtivo);
                System.out.println("Ativo: " + ativo);

                Object dataCriacaoConvertida;
                String dataAtualizacao = campos[8];

                Object dataAtualizacaoConvertida;
                String dataCriacao = campos[7];
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // um formatador de data e hora para o padrão do MySQL
                DateTimeFormatter formatoBrasileiro = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                DateTimeFormatter formatoData =DateTimeFormatter.ofPattern("dd/MM/yyyy");

                

                if (dataCriacao.contains("-") && dataCriacao.contains(" ")){
                    System.out.println("formato ISO Data e Hora");
                    dataCriacaoConvertida = LocalDateTime.parse(dataCriacao, formato);
                    System.out.println("Data convertida: " + dataCriacaoConvertida);

                } else if (dataCriacao.contains("/") && dataCriacao.contains(" ")){
                    System.out.println("formato brasileiro Data e Hora");
                    
                    dataCriacaoConvertida = LocalDateTime.parse(dataCriacao, formatoBrasileiro);
                    System.out.println("Data convertida: " + dataCriacaoConvertida);

                }else if (dataCriacao.contains("/")){
                    System.out.println("formato brasileiro só Data");
                    dataCriacaoConvertida = LocalDate.parse(dataCriacao, formatoData);
                    System.out.println("Data convertida: " + dataCriacaoConvertida);

                }else{
                    System.out.println("Formato de data não reconhecido: " + dataCriacao);
                        continue;
                }
                if (dataAtualizacao.contains("-") && dataAtualizacao.contains(" ")) {
                    dataAtualizacaoConvertida = LocalDateTime.parse(dataAtualizacao, formato);
                            System.out.println("formato ISO Data e Hora");
                        } else if (dataAtualizacao.contains("/") && dataAtualizacao.contains(" ")) {
                            dataAtualizacaoConvertida = LocalDateTime.parse(dataAtualizacao, formatoBrasileiro);
                            System.out.println("formato brasileiro Data e Hora");
                        } else if (dataAtualizacao.contains("/")) {
                            System.out.println("formato brasileiro só Data");
                            dataAtualizacaoConvertida = LocalDate.parse(dataAtualizacao, formatoData);
                        }else{
                            System.out.println("Formato de data não reconhecido: " + dataAtualizacao);
                            continue;
                        }        
                stmt.setObject(8, dataCriacaoConvertida);
                stmt.setObject(9, dataAtualizacaoConvertida);
                stmt.executeUpdate();
            }
            
        }
    }
    }
