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

public class CarrinhosMigracao {

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

    public void migrar(Connection conexao) throws SQLException, IOException {

        int totalLidas = 0;
        int inseridas = 0;
        int rejeitadas = 0;

        String sql = """
                INSERT INTO carrinhos
                (id,cliente_id,status,created_at,updated_at)
                VALUES (?, ?, ?, ?, ?)
                """;

        PreparedStatement stmt = conexao.prepareStatement(sql);

        Path caminho = Path.of("base_legada", "carrinhos.csv");
        BufferedReader leitor = Files.newBufferedReader(caminho);

        String linha = leitor.readLine();

        while ((linha = leitor.readLine()) != null) {

            totalLidas++;

            String[] campos = linha.split(";", -1);

            if (campos.length != 5) {
                rejeitadas++;
                System.out.println("Registro rejeitado!");
                System.out.println("Motivo: quantidade de campos inválida.");
                continue;
            }

            if (campos[1].isEmpty()
                    || campos[2].isEmpty()) {

                rejeitadas++;
                System.out.println("Registro rejeitado!");
                System.out.println("Motivo: campo obrigatório vazio.");
                continue;
            }

            try {

                int id = Integer.parseInt(campos[0]);

                int cliente_id = Integer.parseInt(campos[1]);
                String status = campos[2];

                LocalDateTime dataCriacao =
                        converterData(campos[3]);

                LocalDateTime dataAtualizacao =
                        converterData(campos[4]);

                stmt.setInt(1, id);
                stmt.setInt(2, cliente_id);
                stmt.setString(3, status);

                stmt.setObject(4, dataCriacao);
                stmt.setObject(5, dataAtualizacao);

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
