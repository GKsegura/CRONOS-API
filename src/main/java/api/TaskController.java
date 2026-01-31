package api;

import com.google.gson.Gson;
import entities.Task;
import repository.DiaRepository;
import spark.Spark;

/**
 * Controller responsável pelos endpoints de Tarefas
 *
 * Endpoints:
 * - GET    /api/tarefas/:diaId - Lista tarefas de um dia
 * - POST   /api/tarefas/:diaId - Cria nova tarefa
 * - PUT    /api/tarefas/:id    - Atualiza tarefa
 * - DELETE /api/tarefas/:id    - Remove tarefa
 */
public class TaskController {

    private final DiaRepository repo = new DiaRepository();

    public TaskController(Gson gson) {
        setupRoutes(gson);
    }

    private void setupRoutes(Gson gson) {
        // Listar tarefas de um dia
        Spark.get("/api/tarefas/:diaId", (req, res) -> {
            res.type("application/json");
            try {
                long diaId = Long.parseLong(req.params(":diaId"));
                var dia = repo.findById(diaId);

                if (dia == null) {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("Dia não encontrado"));
                }

                return gson.toJson(dia.getTarefas());

            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(new ErrorResponse("ID inválido"));
            } catch (Exception e) {
                return handleError(res, 500, "Erro ao listar tarefas: " + e.getMessage(), gson, e);
            }
        });

        // Criar nova tarefa
        Spark.post("/api/tarefas/:diaId", (req, res) -> {
            res.type("application/json");
            try {
                long diaId = Long.parseLong(req.params(":diaId"));

                var dia = repo.findById(diaId);
                if (dia == null) {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("Dia não encontrado"));
                }

                Task task = gson.fromJson(req.body(), Task.class);

                // Validar descrição
                if (task.getDescricao() == null || task.getDescricao().trim().isEmpty()) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Descrição da tarefa é obrigatória"));
                }

                Task novaTask = repo.insertTask(task, diaId);
                System.out.println("✓ Tarefa criada: ID " + novaTask.getId() + " - " + novaTask.getDescricao());

                res.status(201);
                return gson.toJson(novaTask);

            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(new ErrorResponse("ID inválido"));
            } catch (Exception e) {
                return handleError(res, 500, "Erro ao criar tarefa: " + e.getMessage(), gson, e);
            }
        });

        // Atualizar tarefa
        Spark.put("/api/tarefas/:id", (req, res) -> {
            res.type("application/json");
            try {
                long id = Long.parseLong(req.params(":id"));

                Task task = gson.fromJson(req.body(), Task.class);
                task.setId(id);

                // Validar descrição
                if (task.getDescricao() == null || task.getDescricao().trim().isEmpty()) {
                    res.status(400);
                    return gson.toJson(new ErrorResponse("Descrição da tarefa é obrigatória"));
                }

                repo.updateTask(task);
                System.out.println("✓ Tarefa atualizada: ID " + id);

                return gson.toJson(task);

            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(new ErrorResponse("ID inválido"));
            } catch (Exception e) {
                return handleError(res, 500, "Erro ao atualizar tarefa: " + e.getMessage(), gson, e);
            }
        });

        // Deletar tarefa
        Spark.delete("/api/tarefas/:id", (req, res) -> {
            res.type("application/json");
            try {
                long id = Long.parseLong(req.params(":id"));

                repo.deleteTaskById(id);
                System.out.println("✓ Tarefa removida: ID " + id);

                return gson.toJson(new SuccessResponse("Tarefa removida com sucesso"));

            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(new ErrorResponse("ID inválido"));
            } catch (Exception e) {
                return handleError(res, 500, "Erro ao remover tarefa: " + e.getMessage(), gson, e);
            }
        });
    }

    private String handleError(spark.Response res, int status, String message, Gson gson, Exception e) {
        System.err.println(message);
        e.printStackTrace();
        res.status(status);
        return gson.toJson(new ErrorResponse(message));
    }

    // Response classes
    static class ErrorResponse {
        final String error;
        ErrorResponse(String error) {
            this.error = error;
        }
    }

    static class SuccessResponse {
        final String message;
        SuccessResponse(String message) {
            this.message = message;
        }
    }
}