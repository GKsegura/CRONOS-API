package api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entities.Dia;
import repository.DiaRepository;
import spark.Spark;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controller responsável pelos endpoints de Dias
 *
 * Endpoints:
 * - GET    /api/dias          - Lista todos os dias
 * - GET    /api/dias/:id      - Busca dia por ID
 * - GET    /api/dias/data/:data - Busca dia por data (dd/MM/yyyy)
 * - POST   /api/dias          - Cria novo dia
 * - PUT    /api/dias/:id      - Atualiza dia existente
 * - DELETE /api/dias/:id      - Remove dia
 */
public class DiaController {

    private final DiaRepository repo = new DiaRepository();
    private static final DateTimeFormatter FORMATTER_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public DiaController(Gson gson) {
        setupRoutes(gson);
    }

    private void setupRoutes(Gson gson) {
        // Listar todos os dias
        Spark.get("/api/dias", (req, res) -> {
            res.type("application/json");
            try {
                return gson.toJson(repo.findAll());
            } catch (Exception e) {
                return handleError(res, 500, "Erro ao listar dias: " + e.getMessage(), gson, e);
            }
        });

        // Criar novo dia
        Spark.post("/api/dias", (req, res) -> {
            res.type("application/json");
            try {
                JsonObject jsonObj = gson.fromJson(req.body(), JsonObject.class);

                String dataFormatada = parseData(jsonObj);

                // Verificar se dia já existe
                if (dataFormatada != null) {
                    for (Dia d : repo.findAll()) {
                        if (d.getDataFormatada().equals(dataFormatada)) {
                            System.out.println("ℹ Dia já existe: ID " + d.getId() + " - Retornando existente");
                            return gson.toJson(d);
                        }
                    }
                }

                Dia dia = new Dia();
                if (dataFormatada != null) {
                    dia.setData(dataFormatada);
                }

                // Configurar horários opcionais
                setTimeIfPresent(dia::setInicioTrabalho, jsonObj, "inicioTrabalho");
                setTimeIfPresent(dia::setFimTrabalho, jsonObj, "fimTrabalho");
                setTimeIfPresent(dia::setInicioAlmoco, jsonObj, "inicioAlmoco");
                setTimeIfPresent(dia::setFimAlmoco, jsonObj, "fimAlmoco");

                Dia diaInserido = repo.insertDia(dia);
                System.out.println("✓ Dia criado com sucesso: ID " + diaInserido.getId());

                res.status(201);
                return gson.toJson(diaInserido);

            } catch (Exception e) {
                return handleError(res, 500, "Erro ao criar dia: " + e.getMessage(), gson, e);
            }
        });

        // Buscar dia por ID
        Spark.get("/api/dias/:id", (req, res) -> {
            res.type("application/json");
            try {
                long id = Long.parseLong(req.params(":id"));
                Dia dia = repo.findById(id);

                if (dia == null) {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("Dia não encontrado"));
                }

                return gson.toJson(dia);
            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(new ErrorResponse("ID inválido"));
            } catch (Exception e) {
                return handleError(res, 500, "Erro ao buscar dia: " + e.getMessage(), gson, e);
            }
        });

        // Buscar dia por data
        Spark.get("/api/dias/data/:data", (req, res) -> {
            res.type("application/json");
            try {
                String dataParam = req.params(":data");

                for (Dia d : repo.findAll()) {
                    if (d.getDataFormatada().equals(dataParam)) {
                        return gson.toJson(d);
                    }
                }

                res.status(404);
                return gson.toJson(new ErrorResponse("Dia não encontrado para a data: " + dataParam));

            } catch (Exception e) {
                return handleError(res, 500, "Erro ao buscar dia: " + e.getMessage(), gson, e);
            }
        });

        // Atualizar dia
        Spark.put("/api/dias/:id", (req, res) -> {
            res.type("application/json");
            try {
                long id = Long.parseLong(req.params(":id"));

                Dia diaExistente = repo.findById(id);
                if (diaExistente == null) {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("Dia não encontrado"));
                }

                JsonObject jsonObj = gson.fromJson(req.body(), JsonObject.class);
                diaExistente.setId(id);

                // Atualizar horários (permitir null para remover)
                updateTimeIfPresent(diaExistente::setInicioTrabalho, jsonObj, "inicioTrabalho");
                updateTimeIfPresent(diaExistente::setFimTrabalho, jsonObj, "fimTrabalho");
                updateTimeIfPresent(diaExistente::setInicioAlmoco, jsonObj, "inicioAlmoco");
                updateTimeIfPresent(diaExistente::setFimAlmoco, jsonObj, "fimAlmoco");

                repo.updateDia(diaExistente);
                System.out.println("✓ Dia atualizado: ID " + id);

                return gson.toJson(diaExistente);

            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(new ErrorResponse("ID inválido"));
            } catch (Exception e) {
                return handleError(res, 500, "Erro ao atualizar dia: " + e.getMessage(), gson, e);
            }
        });

        // Deletar dia
        Spark.delete("/api/dias/:id", (req, res) -> {
            res.type("application/json");
            try {
                long id = Long.parseLong(req.params(":id"));
                boolean deleted = repo.deleteDia(id);

                if (!deleted) {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("Dia não encontrado"));
                }

                System.out.println("✓ Dia removido: ID " + id);
                return gson.toJson(new SuccessResponse("Dia removido com sucesso"));

            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(new ErrorResponse("ID inválido"));
            } catch (Exception e) {
                return handleError(res, 500, "Erro ao remover dia: " + e.getMessage(), gson, e);
            }
        });
    }

    // Métodos auxiliares

    private String parseData(JsonObject jsonObj) {
        if (!jsonObj.has("data") || jsonObj.get("data").isJsonNull()) {
            return null;
        }

        String dataStr = jsonObj.get("data").getAsString();
        LocalDate data;

        if (dataStr.contains("-")) {
            data = LocalDate.parse(dataStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            data = LocalDate.parse(dataStr, FORMATTER_BR);
        }

        return data.format(FORMATTER_BR);
    }

    private void setTimeIfPresent(java.util.function.Consumer<java.time.LocalTime> setter,
                                  JsonObject jsonObj, String fieldName) {
        if (jsonObj.has(fieldName) && !jsonObj.get(fieldName).isJsonNull()) {
            String timeStr = jsonObj.get(fieldName).getAsString();
            setter.accept(parseTime(timeStr));
        }
    }

    private void updateTimeIfPresent(java.util.function.Consumer<java.time.LocalTime> setter,
                                     JsonObject jsonObj, String fieldName) {
        if (jsonObj.has(fieldName)) {
            if (jsonObj.get(fieldName).isJsonNull()) {
                setter.accept(null);
            } else {
                setter.accept(parseTime(jsonObj.get(fieldName).getAsString()));
            }
        }
    }

    private java.time.LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return null;
        }

        if (timeStr.length() == 5) {
            return java.time.LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            return java.time.LocalTime.parse(timeStr, DateTimeFormatter.ISO_LOCAL_TIME);
        }
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