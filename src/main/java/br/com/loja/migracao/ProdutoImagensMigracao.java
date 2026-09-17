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


public class ProdutoImagensMigracao {
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
            INSERT INTO produto_imagens
            (id,produto_id,url,ordem,principal,created_at)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    PreparedStatement stmt = conexao.prepareStatement(sql);

    Path caminho = Path.of("base_legada", "produto_imagens.csv");
    BufferedReader leitor = Files.newBufferedReader(caminho);

    String linha = leitor.readLine();

    while ((linha = leitor.readLine()) != null) {

        totalLidas++;

        String[] campos = linha.split(";", -1);

        if (campos.length != 6) {
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
            int produto_Id = Integer.parseInt(campos[1]);
            String url = campos[2];
            int ordem = Integer.parseInt(campos[3]);
            int principal = Integer.parseInt(campos[4]);

            LocalDateTime dataCriacao =
                    converterData(campos[5]);


            stmt.setInt(1, id);
            stmt.setInt(2, produto_Id);
            stmt.setString(3, url);
            stmt.setInt(4, ordem);
            stmt.setInt(5, principal);
            stmt.setObject(6, dataCriacao);

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
