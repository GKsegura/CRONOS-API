package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class TableCreator {
    private static final Map<String, String> TABLES = new LinkedHashMap<>();

    static {
        TABLES.put("dias",
                "CREATE TABLE IF NOT EXISTS dias (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "data TEXT NOT NULL," +
                        "inicioTrabalho TEXT," +
                        "fimTrabalho TEXT," +
                        "inicioAlmoco TEXT," +
                        "fimAlmoco TEXT);");

        TABLES.put("tarefas",
                "CREATE TABLE IF NOT EXISTS tarefas (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "descricao TEXT NOT NULL," +
                        "categoria TEXT," +
                        "cliente TEXT," +
                        "duracaoMin INTEGER," +
                        "obs TEXT," +
                        "apontado INTEGER DEFAULT 0," +
                        "dia_id INTEGER," +
                        "FOREIGN KEY(dia_id) REFERENCES dias(id) ON DELETE CASCADE);");

        TABLES.put("backlog",
                "CREATE TABLE IF NOT EXISTS backlog (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "descricao TEXT NOT NULL," +
                        "categoria TEXT," +
                        "cliente TEXT," +
                        "duracaoMin INTEGER," +
                        "obs TEXT," +
                        "prioridade INTEGER DEFAULT 0," +
                        "dataCriacao TEXT NOT NULL," +
                        "dataLimite TEXT);");
    }

    public static void criarTabelas() {
        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String sql : TABLES.values()) {
                stmt.execute(sql);
            }

            // Garante a coluna mesmo em bancos já existentes
            adicionarColunaSeNaoExistir(stmt);

            System.out.println("Tabelas criadas/verificadas com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro crítico ao criar as tabelas: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Roda ALTER TABLE de forma segura — o SQLite não tem IF NOT EXISTS
     * para colunas, então basta ignorar o erro se já existir.
     */
    private static void adicionarColunaSeNaoExistir(Statement stmt) {
        try {
            stmt.execute("ALTER TABLE backlog ADD COLUMN dataLimite TEXT");
        } catch (SQLException ignored) {
            // Coluna já existe — normal em bancos antigos
        }
    }
}