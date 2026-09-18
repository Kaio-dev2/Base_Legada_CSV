package br.com.loja.migracao;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class EnderecosMigracao {

private LocalDateTime converterData(String data) {

        DateTimeFormatter[] formatos = {
                DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss")
        };

        for (DateTimeFormatter formato : formatos) {
            try {
                return LocalDateTime.parse(data, formato);
            } catch (DateTimeParseException ignored) {
            }
        }

        try {
            return LocalDate.parse(
                    data,
                    DateTimeFormatter.ofPattern("dd/MM/uuuu")
            ).atStartOfDay();

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida: " + data);
        }
    }


    public void migrar(Connection conexao) throws SQLException , IOException {

        int totalLidas = 0;
        int inseridas = 0;
        int rejeitadas = 0;


        String sql = """
        INSERT INTO enderecos
        (id, cliente_id, tipo, cep, logradouro, numero, complemento, bairro, cidade, estado, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        PreparedStatement stmt = conexao.prepareStatement(sql);


        Path caminho = Path.of("base_legada", "enderecos.csv");
        BufferedReader leitor = Files.newBufferedReader(caminho);

        String linha = leitor.readLine();

        while ((linha = leitor.readLine()) != null) {
            totalLidas++;

            String[] campos = linha.split(";" , -1);


                if (campos.length != 11) {
                rejeitadas++;
                System.out.println("Registro rejeitado!");
                System.out.println("Motivo: quantidade de campos inválida.");
                continue;
                }

                if (campos[1].isEmpty()
                        || campos[2].isEmpty()
                        || campos[5].isEmpty()) {

                rejeitadas++;
                System.out.println("Registro rejeitado!");
                System.out.println("Motivo: campo obrigatório vazio.");
                continue;
                }

                try {
 

                    int id = Integer.parseInt(campos[0]);
                    int clienteId = Integer.parseInt(campos[1]);
                    String tipo = campos[2];
                    String cep = campos[3];
                    String logradouro = campos[4];
                    String numero = campos[5];
                    String complemento = campos[6];
                    String bairro = campos[7];
                    String cidade = campos[8];
                    String estado = campos[9];
                    String created_at = campos [10];


                    stmt.setInt(1, id);
                    stmt.setInt(2, clienteId);
                    stmt.setString(3, tipo);
                    stmt.setString(4, cep);
                    stmt.setString(5, logradouro);
                    stmt.setString(6, numero);
                    stmt.setString(7, complemento);
                    stmt.setString(8, bairro);
                    stmt.setString(9, cidade);
                    stmt.setString(10, estado);

                    LocalDateTime dataCriacaoConvertida =
                    converterData(created_at);

                    stmt.setObject(11, dataCriacaoConvertida);

                    stmt.executeUpdate();
                    
                    inseridas++;

                    System.out.println("Registro inserido: ID " + id);


                        } catch (SQLException | RuntimeException e) {
                            rejeitadas++;

                            System.out.println("Registro rejeitado!");
                            System.out.println("Motivo: " + e.getMessage());
                        }
                    }
                    
        
        System.out.println();
        System.out.println("===== RESUMO DA MIGRAÇÃO =====");
        System.out.println("Total lidas: " + totalLidas);
        System.out.println("Inseridas: " + inseridas);
        System.out.println("Rejeitadas: " + rejeitadas);
    }

    
}
