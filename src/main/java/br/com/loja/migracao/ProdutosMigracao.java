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

            public class ProdutosMigracao {
                
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
                            INSERT INTO produtos
                            (id,categoria_id,nome,slug,descricao,sku,preco,preco_promocional,peso,altura,largura,comprimento,ativo,created_at,updated_at)
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?)
                            """;

                    PreparedStatement stmt = conexao.prepareStatement(sql);

                    Path caminho = Path.of("base_legada", "produtos.csv");
                    BufferedReader leitor = Files.newBufferedReader(caminho);

                    String linha = leitor.readLine();

                    while ((linha = leitor.readLine()) != null) {

                        totalLidas++;

                        String[] campos = linha.split(";", -1);

                        if (campos.length != 15) {
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
                            int categoria_Id = Integer.parseInt(campos[1]);
                            String nome = campos[2];
                            String slug = campos[3];
                            String descricao = campos[4];
                            String sku = campos[5];

                            BigDecimal preco = new BigDecimal(campos[6].replace(",", "."));

                            BigDecimal precoPromocional = campos[7].isEmpty()
                            ? null
                            : new BigDecimal(campos[7].replace(",", "."));

                            BigDecimal peso = new BigDecimal(campos[8].replace(",", "."));
                            BigDecimal altura = new BigDecimal(campos[9].replace(",", "."));
                            BigDecimal largura = new BigDecimal(campos[10].replace(",", "."));
                            BigDecimal comprimento = new BigDecimal(campos[11].replace(",", "."));
                            

                            boolean ativo =
                                    campos[12].equals("1")
                                    || campos[12].equalsIgnoreCase("S");

                            LocalDateTime dataCriacao =
                                    converterData(campos[13]);

                            LocalDateTime dataAtualizacao =
                                    converterData(campos[14]);



                            stmt.setInt(1, id);
                            stmt.setInt(2, categoria_Id);
                            stmt.setString(3, nome);
                            stmt.setString(4, slug);
                            stmt.setString(5, descricao);
                            stmt.setString(6,sku);
                            stmt.setBigDecimal(7,preco);

                            if (precoPromocional == null) {
                                stmt.setNull(8, java.sql.Types.DECIMAL);
                            } else {
                                stmt.setBigDecimal(8, precoPromocional);
                            }
                            
                            stmt.setBigDecimal(9,peso);
                            stmt.setBigDecimal(10,altura);
                            stmt.setBigDecimal(11,largura);
                            stmt.setBigDecimal(12,comprimento);

                            stmt.setBoolean(13, ativo);
                            stmt.setObject(14, dataCriacao);
                            stmt.setObject(15, dataAtualizacao);

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
