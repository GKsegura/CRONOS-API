package api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entities.BacklogTask;
import entities.Categoria;
import entities.Task;
import repositories.BacklogRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static spark.Spark.*;

public class BacklogController {

    private final BacklogRepository repo = new BacklogRepository();

    public BacklogController(Gson gson) {
        setupRoutes(gson);
    }

    private void setupRoutes(Gson gson) {

        // GET /api/backlog — lista tudo ordenado por prioridade
        get("/api/backlog", (req, res) -> {
            res.type("application/json");
            try {
                return gson.toJson(repo.findAll());
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return erro(gson, "Erro ao buscar backlog: " + e.getMessage());
            }
        });

        // POST /api/backlog — cria novo item
        post("/api/backlog", (req, res) -> {
            res.type("application/json");
            try {
                JsonObject body = gson.fromJson(req.body(), JsonObject.class);

                if (!body.has("descricao") || body.get("descricao").getAsString().isBlank()) {
                    res.status(400);
                    return erro(gson, "Campo 'descricao' é obrigatório");
                }

                BacklogTask backlog = new BacklogTask();
                backlog.setDescricao(body.get("descricao").getAsString());
                backlog.setCliente(getStringOuNull(body, "cliente"));
                backlog.setObs(getStringOuNull(body, "obs"));
                backlog.setDataCriacao(java.time.LocalDate.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                if (body.has("duracaoMin") && !body.get("duracaoMin").isJsonNull())
                    backlog.setDuracaoMin(body.get("duracaoMin").getAsLong());

                if (body.has("prioridade") && !body.get("prioridade").isJsonNull())
                    backlog.setPrioridade(body.get("prioridade").getAsInt());

                if (body.has("dataLimite") && !body.get("dataLimite").isJsonNull())
                    backlog.setDataLimite(body.get("dataLimite").getAsString());

                if (body.has("categoria") && !body.get("categoria").isJsonNull()) {
                    try { backlog.setCategoria(Categoria.valueOf(body.get("categoria").getAsString())); }
                    catch (IllegalArgumentException ignored) {}
                }

                res.status(201);
                return gson.toJson(repo.save(backlog));

            } catch (Exception e) {
                res.status(500);
                return erro(gson, "Erro ao criar backlog: " + e.getMessage());
            }
        });

        // PUT /api/backlog/reordenar — precisa vir ANTES de /api/backlog/:id
        // para o Spark não confundir "reordenar" com um ID
        put("/api/backlog/reordenar", (req, res) -> {
            res.type("application/json");
            try {
                JsonArray array = gson.fromJson(req.body(), JsonArray.class);
                List<Long> ids = new ArrayList<>();
                array.forEach(el -> ids.add(el.getAsLong()));

                repo.reordenar(ids);
                return gson.toJson(Collections.singletonMap("message", "Reordenado com sucesso"));

            } catch (Exception e) {
                res.status(500);
                return erro(gson, "Erro ao reordenar: " + e.getMessage());
            }
        });

        // PUT /api/backlog/:id — edita item
        put("/api/backlog/:id", (req, res) -> {
            res.type("application/json");
            try {
                Long id = Long.parseLong(req.params(":id"));
                JsonObject body = gson.fromJson(req.body(), JsonObject.class);

                BacklogTask dados = new BacklogTask();
                if (body.has("descricao"))  dados.setDescricao(body.get("descricao").getAsString());
                if (body.has("cliente"))    dados.setCliente(getStringOuNull(body, "cliente"));
                if (body.has("obs"))        dados.setObs(getStringOuNull(body, "obs"));
                if (body.has("dataLimite")) dados.setDataLimite(getStringOuNull(body, "dataLimite"));

                if (body.has("duracaoMin") && !body.get("duracaoMin").isJsonNull())
                    dados.setDuracaoMin(body.get("duracaoMin").getAsLong());

                if (body.has("categoria") && !body.get("categoria").isJsonNull()) {
                    try { dados.setCategoria(Categoria.valueOf(body.get("categoria").getAsString())); }
                    catch (IllegalArgumentException ignored) {}
                }

                BacklogTask atualizado = repo.update(id, dados);
                if (atualizado == null) { res.status(404); return erro(gson, "Item não encontrado"); }

                return gson.toJson(atualizado);

            } catch (NumberFormatException e) {
                res.status(400); return erro(gson, "ID inválido");
            } catch (Exception e) {
                res.status(500); return erro(gson, "Erro ao atualizar: " + e.getMessage());
            }
        });

        // DELETE /api/backlog/:id — remove item
        delete("/api/backlog/:id", (req, res) -> {
            res.type("application/json");
            try {
                Long id = Long.parseLong(req.params(":id"));
                boolean removido = repo.delete(id);
                if (!removido) { res.status(404); return erro(gson, "Item não encontrado"); }
                return gson.toJson(Collections.singletonMap("message", "Item removido do backlog"));

            } catch (NumberFormatException e) {
                res.status(400); return erro(gson, "ID inválido");
            } catch (Exception e) {
                res.status(500); return erro(gson, "Erro ao remover: " + e.getMessage());
            }
        });

        // POST /api/backlog/:id/converter/:diaId — vira task do dia
        post("/api/backlog/:id/converter/:diaId", (req, res) -> {
            res.type("application/json");
            try {
                Long backlogId = Long.parseLong(req.params(":id"));
                Long diaId     = Long.parseLong(req.params(":diaId"));

                Task novaTarefa = repo.converterParaDia(backlogId, diaId);
                if (novaTarefa == null) { res.status(404); return erro(gson, "Item do backlog não encontrado"); }

                res.status(201);
                return gson.toJson(novaTarefa);

            } catch (NumberFormatException e) {
                res.status(400); return erro(gson, "ID inválido");
            } catch (Exception e) {
                res.status(500); return erro(gson, "Erro ao converter: " + e.getMessage());
            }
        });
    }

    private String erro(Gson gson, String msg) {
        return gson.toJson(Collections.singletonMap("error", msg));
    }

    private String getStringOuNull(JsonObject body, String campo) {
        if (!body.has(campo) || body.get(campo).isJsonNull()) return null;
        String val = body.get(campo).getAsString();
        return val.isBlank() ? null : val;
    }
}