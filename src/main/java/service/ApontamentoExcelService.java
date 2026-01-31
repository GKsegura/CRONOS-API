package service;

import entities.Dia;
import entities.Task;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ApontamentoExcelService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");

    public byte[] gerarPlanilhaApontamentos(List<Dia> dias, String nomeUsuario) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(nomeUsuario);

            // Estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dataStyleGray = createDataStyleGray(workbook);

            int rowNum = 0;

            // Cabeçalho
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {
                    "ATIVIDADE",
                    "CATEGORIA",
                    "DATA",
                    "HORAS",
                    "CLIENTE",
                    "OBSERVAÇÕES",
                    "APONTADO"
            };

            for (int i = 0; i < headers.length; i++) {
                createCell(headerRow, i, headers[i], headerStyle);
            }

            // Dados - alternar cores por dia
            LocalDate diaAtual = null;
            boolean grayDay = false;

            for (Dia dia : dias) {
                // Verificar se mudou de dia
                if (diaAtual == null || !diaAtual.equals(dia.getData())) {
                    diaAtual = dia.getData();
                    grayDay = !grayDay; // Alternar cor quando muda de dia
                }

                CellStyle currentStyle = grayDay ? dataStyleGray : dataStyle;

                for (Task task : dia.getTarefas()) {
                    Row row = sheet.createRow(rowNum++);

                    // Atividade
                    createCell(row, 0, task.getDescricao(), currentStyle);

                    // Categoria
                    createCell(row, 1, task.getCategoria() != null ? task.getCategoria().toString() : "", currentStyle);

                    // Data
                    createCell(row, 2, dia.getData().format(DATE_FORMATTER), currentStyle);

                    // Horas
                    String horas = formatarHoras(task.getDuracaoMin());
                    createCell(row, 3, "0:00".equals(horas) ? "" : horas, currentStyle);

                    // Cliente
                    createCell(row, 4, task.getCliente() != null ? task.getCliente() : "NEXUM - INTERNA", currentStyle);

                    // Observações
                    createCell(row, 5, task.getObs() != null ? task.getObs() : "", currentStyle);

                    // Apontado
                    createCell(row, 6, task.isApontado() ? "SIM" : "NÃO", currentStyle);
                }
            }

            // Ajustar largura das colunas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                int currentWidth = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, currentWidth + 1000);
            }

            // Garantir largura mínima para observações
            if (sheet.getColumnWidth(5) < 6000) {
                sheet.setColumnWidth(5, 6000);
            }

            // Converter para bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // Cor de fundo verde escuro
        style.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Fonte branca e negrito
        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);

        // Bordas
        setBorders(style);

        // Alinhamento
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        setBorders(style);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true); // Quebrar texto em células longas
        return style;
    }

    private CellStyle createDataStyleGray(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void setBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private Cell createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        return cell;
    }

    private String formatarHoras(Long minutos) {
        if (minutos == null) return "0:00";
        long horas = minutos / 60;
        long mins = minutos % 60;
        return String.format("%d:%02d", horas, mins);
    }
}