package br.com.loja.migracao;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PedidosMigracao {
    
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
                INSERT INTO pedidos
                (id,cliente_id,endereco_id,status,subtotal,desconto,frete,total,forma_pagamento,created_at,updated_at)
                VALUES (?, ?, ?, ?, ?,?,?,?,?,?,?)
                """;

        PreparedStatement stmt = conexao.prepareStatement(sql);

        Path caminho = Path.of("base_legada", "pedidos.csv");
        BufferedReader leitor = Files.newBufferedReader(caminho);

        String linha = leitor.readLine();

        while ((linha = leitor.readLine()) != null) {

            totalLidas++;

            String[] campos = linha.split(";", -1);

            if (campos.length != 11) {
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
                int endereco_id = Integer.parseInt(campos[2]);
                String status = campos[3];
                BigDecimal subtotal = new BigDecimal(campos[4].replace(",", "."));
                BigDecimal desconto = new BigDecimal(campos[5].replace(",", "."));
                BigDecimal frete = new BigDecimal(campos[6].replace(",", "."));
                BigDecimal total = new BigDecimal(campos[7].replace(",", "."));
                String forma_pagamento = campos[8];

//BigDecimal total = new BigDecimal(campos[7].replace(",", "."));

                LocalDateTime dataCriacao =
                        converterData(campos[9]);

                LocalDateTime dataAtualizacao =
                        converterData(campos[10]);

                stmt.setInt(1, id);
                stmt.setInt(2, cliente_id);
                stmt.setInt(3, endereco_id);
                stmt.setString(4, status);
                stmt.setBigDecimal(5, subtotal);
                stmt.setBigDecimal(6, desconto);
                stmt.setBigDecimal(7, frete);
                stmt.setBigDecimal(8,total);
                stmt.setString(9,forma_pagamento);

                stmt.setObject(10, dataCriacao);
                stmt.setObject(11, dataAtualizacao);

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
