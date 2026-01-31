package api;

import com.google.gson.Gson;
import entities.Categoria;
import spark.Spark;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CategoriaController {

    public CategoriaController(Gson gson) {
        Spark.get("/api/categorias", (req, res) -> {
            res.type("application/json");
            try {
                List<String> categorias = Arrays.stream(Categoria.values())
                        .map(Enum::name)
                        .collect(Collectors.toList());

                return gson.toJson(categorias);

            } catch (Exception e) {
                System.err.println("Erro ao listar categorias: " + e.getMessage());
                res.status(500);
                return gson.toJson(new ErrorResponse("Erro ao listar categorias: " + e.getMessage()));
            }
        });
    }

    static class ErrorResponse {
        final String error;
        ErrorResponse(String error) {
            this.error = error;
        }
    }
}