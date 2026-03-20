package repositories;

import database.SQLiteConnection;
import entities.BacklogTask;
import entities.Categoria;
import entities.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BacklogRepository {

    // ---------------------------------------------------------------
    // LISTAGEM
    // ---------------------------------------------------------------

    public List<BacklogTask> findAll() throws SQLException {
        List<BacklogTask> lista = new ArrayList<>();
        String sql = "SELECT * FROM backlog ORDER BY prioridade ASC";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public BacklogTask findById(Long id) throws SQLException {
        String sql = "SELECT * FROM backlog WHERE id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ---------------------------------------------------------------
    // CRIAÇÃO
    // ---------------------------------------------------------------

    public BacklogTask save(BacklogTask backlog) throws SQLException {
        if (backlog.getPrioridade() == 0) {
            backlog.setPrioridade(getProximaPrioridade());
        }

        String sql = "INSERT INTO backlog (descricao, categoria, cliente, duracaoMin, obs, prioridade, dataCriacao, dataLimite)VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, backlog.getDescricao());
            stmt.setString(2, backlog.getCategoria() != null ? backlog.getCategoria().name() : null);
            stmt.setString(3, backlog.getCliente());
            stmt.setObject(4, backlog.getDuracaoMin());
            stmt.setString(5, backlog.getObs());
            stmt.setInt(6, backlog.getPrioridade());
            stmt.setString(7, backlog.getDataCriacao());
            stmt.setString(8, backlog.getDataLimite());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) backlog.setId(keys.getLong(1));
            }
        }
        return backlog;
    }

    // ---------------------------------------------------------------
    // ATUALIZAÇÃO
    // ---------------------------------------------------------------

    public BacklogTask update(Long id, BacklogTask dados) throws SQLException {
        BacklogTask existente = findById(id);
        if (existente == null) return null;

        if (dados.getDescricao() != null)  existente.setDescricao(dados.getDescricao());
        if (dados.getCategoria() != null)  existente.setCategoria(dados.getCategoria());
        if (dados.getCliente() != null)    existente.setCliente(dados.getCliente());
        if (dados.getDuracaoMin() != null) existente.setDuracaoMin(dados.getDuracaoMin());
        if (dados.getObs() != null)        existente.setObs(dados.getObs());
        if (dados.getDataLimite() != null) existente.setDataLimite(dados.getDataLimite());

        String sql = "UPDATE backlog SET descricao = ?, categoria = ?, cliente = ?, duracaoMin = ?, obs = ?, dataLimite = ? WHERE id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, existente.getDescricao());
            stmt.setString(2, existente.getCategoria() != null ? existente.getCategoria().name() : null);
            stmt.setString(3, existente.getCliente());
            stmt.setObject(4, existente.getDuracaoMin());
            stmt.setString(5, existente.getObs());
            stmt.setString(6, existente.getDataLimite());
            stmt.setLong(7, id);
            stmt.executeUpdate();
        }
        return existente;
    }

    /**
     * Recebe lista de IDs na nova ordem e atualiza prioridade de cada um (1, 2, 3...).
     */
    public void reordenar(List<Long> idsOrdenados) throws SQLException {
        String sql = "UPDATE backlog SET prioridade = ? WHERE id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < idsOrdenados.size(); i++) {
                stmt.setInt(1, i + 1);
                stmt.setLong(2, idsOrdenados.get(i));
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    // ---------------------------------------------------------------
    // REMOÇÃO
    // ---------------------------------------------------------------

    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM backlog WHERE id = ?";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // ---------------------------------------------------------------
    // CONVERSÃO → TASK DO DIA
    // ---------------------------------------------------------------

    /**
     * Converte um item do backlog em Task do dia informado.
     * Insere a task e remove do backlog na mesma transação.
     */
    public Task converterParaDia(Long backlogId, Long diaId) throws SQLException {
        BacklogTask backlog = findById(backlogId);
        if (backlog == null) return null;

        String insertTask = "INSERT INTO tarefas (descricao, categoria, cliente, duracaoMin, obs, apontado, dia_id)VALUES (?, ?, ?, ?, ?, 0, ?)";

        Task novaTarefa = new Task();

        try (Connection conn = SQLiteConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = conn.prepareStatement(insertTask, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, backlog.getDescricao());
                    stmt.setString(2, backlog.getCategoria() != null ? backlog.getCategoria().name() : null);
                    stmt.setString(3, backlog.getCliente());
                    stmt.setObject(4, backlog.getDuracaoMin());
                    stmt.setString(5, backlog.getObs());
                    stmt.setLong(6, diaId);
                    stmt.executeUpdate();

                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (keys.next()) novaTarefa.setId(keys.getLong(1));
                    }
                }

                try (PreparedStatement del = conn.prepareStatement("DELETE FROM backlog WHERE id = ?")) {
                    del.setLong(1, backlogId);
                    del.executeUpdate();
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }

        novaTarefa.setDescricao(backlog.getDescricao());
        novaTarefa.setCategoria(backlog.getCategoria());
        novaTarefa.setCliente(backlog.getCliente());
        novaTarefa.setDuracaoMin(backlog.getDuracaoMin());
        novaTarefa.setObs(backlog.getObs());
        novaTarefa.setApontado(false);

        return novaTarefa;
    }

    // ---------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------

    private int getProximaPrioridade() throws SQLException {
        String sql = "SELECT COALESCE(MAX(prioridade), 0) + 1 FROM backlog";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 1;
        }
    }

    private BacklogTask mapRow(ResultSet rs) throws SQLException {
        BacklogTask b = new BacklogTask();
        b.setId(rs.getLong("id"));
        b.setDescricao(rs.getString("descricao"));
        b.setCliente(rs.getString("cliente"));
        b.setDuracaoMin(rs.getObject("duracaoMin", Long.class));
        b.setObs(rs.getString("obs"));
        b.setPrioridade(rs.getInt("prioridade"));
        b.setDataCriacao(rs.getString("dataCriacao"));
        b.setDataLimite(rs.getString("dataLimite"));

        String cat = rs.getString("categoria");
        if (cat != null) {
            try { b.setCategoria(Categoria.valueOf(cat)); }
            catch (IllegalArgumentException ignored) {}
        }

        return b;
    }
}