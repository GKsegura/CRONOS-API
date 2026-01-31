package api;

import com.google.gson.Gson;
import entities.Cliente;
import spark.Spark;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClienteController {

    public ClienteController(Gson gson) {

        Spark.get("/api/clientes", (req, res) -> {
            res.type("application/json");
            try {
                List<Map<String, String>> clientes = Arrays.stream(Cliente.values())
                        .map(c -> {
                            Map<String, String> map = new HashMap<>();
                            map.put("valor", c.name());
                            map.put("nome", c.getNome());
                            return map;
                        })
                        .sorted((a, b) -> a.get("nome").compareTo(b.get("nome")))
                        .collect(Collectors.toList());

                return gson.toJson(clientes);

            } catch (Exception e) {
                System.err.println("Erro ao listar clientes: " + e.getMessage());
                res.status(500);
                return gson.toJson(new ErrorResponse("Erro ao listar clientes: " + e.getMessage()));
            }
        });

        Spark.get("/api/clientes/nomes", (req, res) -> {
            res.type("application/json");
            try {
                List<String> nomes = Arrays.stream(Cliente.values())
                        .map(Cliente::getNome)
                        .sorted()
                        .collect(Collectors.toList());

                return gson.toJson(nomes);

            } catch (Exception e) {
                System.err.println("Erro ao listar nomes de clientes: " + e.getMessage());
                res.status(500);
                return gson.toJson(new ErrorResponse("Erro ao listar nomes: " + e.getMessage()));
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