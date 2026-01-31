package api;

import com.google.gson.Gson;
import entities.Dia;
import repository.DiaRepository;
import service.ApontamentoExcelService;
import spark.Spark;

import javax.servlet.ServletOutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsável pela exportação de dados
 *
 * Endpoints:
 * - GET /api/export/excel/:mes/:ano - Exporta apontamentos do mês em Excel
 */
public class ExportController {

    private final DiaRepository repo = new DiaRepository();
    private final ApontamentoExcelService excelService = new ApontamentoExcelService();

    public ExportController(Gson gson) {
        setupRoutes(gson);
    }

    private void setupRoutes(Gson gson) {
        // Exportar Excel do mês
        Spark.get("/api/export/excel/:mes/:ano", (req, res) -> {
            try {
                int mes = Integer.parseInt(req.params(":mes"));
                int ano = Integer.parseInt(req.params(":ano"));

                // Validar mês
                if (mes < 1 || mes > 12) {
                    res.status(400);
                    res.type("application/json");
                    return gson.toJson(new ErrorResponse("Mês inválido. Deve ser entre 1 e 12"));
                }

                // Buscar dias do mês
                List<Dia> diasDoMes = repo.findAll().stream()
                        .filter(d -> d.getData() != null &&
                                d.getData().getMonthValue() == mes &&
                                d.getData().getYear() == ano)
                        .sorted((d1, d2) -> d1.getData().compareTo(d2.getData()))
                        .collect(Collectors.toList());

                if (diasDoMes.isEmpty()) {
                    res.status(404);
                    res.type("application/json");
                    return gson.toJson(new ErrorResponse("Nenhum apontamento encontrado para " + mes + "/" + ano));
                }

                // Gerar Excel
                byte[] excelBytes = excelService.gerarPlanilhaApontamentos(diasDoMes, "USUARIO");

                // Configurar resposta
                res.type("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                res.header("Content-Disposition",
                        String.format("attachment; filename=\"Apontamentos_%02d_%d.xlsx\"", mes, ano));

                ServletOutputStream outputStream = res.raw().getOutputStream();
                outputStream.write(excelBytes);
                outputStream.flush();

                System.out.println("✓ Excel gerado com sucesso: " + mes + "/" + ano +
                        " (" + diasDoMes.size() + " dias)");

                return "";

            } catch (NumberFormatException e) {
                res.status(400);
                res.type("application/json");
                return gson.toJson(new ErrorResponse("Mês ou ano inválido"));
            } catch (Exception e) {
                System.err.println("Erro ao gerar planilha: " + e.getMessage());
                e.printStackTrace();
                res.status(500);
                res.type("application/json");
                return gson.toJson(new ErrorResponse("Erro ao gerar planilha: " + e.getMessage()));
            }
        });
    }

    // Response class
    static class ErrorResponse {
        final String error;
        ErrorResponse(String error) {
            this.error = error;
        }
    }
}