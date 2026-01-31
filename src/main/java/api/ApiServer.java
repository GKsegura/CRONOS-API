package api;

import com.google.gson.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import static spark.Spark.*;

/**
 * Servidor principal da API Cronos
 * Configura CORS, JSON serialization e registra os controllers
 */
public class ApiServer {

    private static final int PORT = 8080;

    public static void start() {
        Gson gson = createGsonWithAdapters();

        port(PORT);
        staticFiles.location("/public");
        configureCORS();

        new DiaController(gson);
        new TaskController(gson);
        new ClienteController(gson);
        new CategoriaController(gson);
        new ExportController(gson);

        System.out.println("✓ API Cronos rodando em http://localhost:" + PORT);
        System.out.println("✓ Documentação disponível em /docs");
    }

    private static Gson createGsonWithAdapters() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    private static void configureCORS() {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        // CORS headers for all requests
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });
    }

    /**
     * Adapter para serialização/deserialização de LocalDate
     * Suporta formatos: dd/MM/yyyy e yyyy-MM-dd
     */
    static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private static final DateTimeFormatter FORMATTER_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        private static final DateTimeFormatter FORMATTER_ISO = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public JsonElement serialize(LocalDate src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(FORMATTER_BR));
        }

        @Override
        public LocalDate deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String dateStr = json.getAsString();

            if (dateStr.contains("-")) {
                return LocalDate.parse(dateStr, FORMATTER_ISO);
            } else {
                return LocalDate.parse(dateStr, FORMATTER_BR);
            }
        }
    }

    /**
     * Adapter para serialização/deserialização de LocalTime
     * Suporta formatos: HH:mm e HH:mm:ss
     */
    static class LocalTimeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {
        private static final DateTimeFormatter FORMATTER_SHORT = DateTimeFormatter.ofPattern("HH:mm");
        private static final DateTimeFormatter FORMATTER_ISO = DateTimeFormatter.ISO_LOCAL_TIME;

        @Override
        public JsonElement serialize(LocalTime src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(FORMATTER_SHORT));
        }

        @Override
        public LocalTime deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String timeStr = json.getAsString();

            if (timeStr.length() == 5) {
                return LocalTime.parse(timeStr, FORMATTER_SHORT);
            } else {
                return LocalTime.parse(timeStr, FORMATTER_ISO);
            }
        }
    }
}