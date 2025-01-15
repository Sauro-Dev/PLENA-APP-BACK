package com.plenamente.sgt.service.impl;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.plenamente.sgt.domain.dto.SessionDto.ReportSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class PdfGenerationService {
    @Autowired
    private ResourceLoader resourceLoader;

    private static final float[] SESSION_REPORT_COLUMNS = new float[]{1, 2, 2, 2, 1, 1, 2, 2, 1, 1, 1, 1, 1};

    private Table createSessionReportTable() {
        Table table = new Table(SESSION_REPORT_COLUMNS);
        table.addHeaderCell("ID Sesión");
        table.addHeaderCell("Fecha");
        table.addHeaderCell("Hora Inicio");
        table.addHeaderCell("Hora Fin");
        table.addHeaderCell("Paciente Presente");
        table.addHeaderCell("Razón");
        table.addHeaderCell("Renovar Plan");
        table.addHeaderCell("Reprogramada");
        table.addHeaderCell("Terapeuta Presente");
        table.addHeaderCell("ID Paciente");
        table.addHeaderCell("ID Plan");
        table.addHeaderCell("ID Sala");
        table.addHeaderCell("ID Terapeuta");
        return table;
    }

    private void addSessionReportRow(Table table, ReportSession report) {
        table.addCell(String.valueOf(report.idSession()));
        table.addCell(String.valueOf(report.sessionDate()));
        table.addCell(String.valueOf(report.startTime()));
        table.addCell(String.valueOf(report.endTime()));
        table.addCell(report.patientPresent() ? "Sí" : "No");
        table.addCell(report.reason() == null ? "N/A" : report.reason());
        table.addCell(String.valueOf(report.renewPlan()));
        table.addCell(report.rescheduled() ? "Sí" : "No");
        table.addCell(report.therapistPresent() ? "Sí" : "No");
        table.addCell(String.valueOf(report.patientId()));
        table.addCell(String.valueOf(report.planId()));
        table.addCell(String.valueOf(report.roomId()));
        table.addCell(String.valueOf(report.therapistId()));
    }

    private byte[] generatePdf(String title, Table table) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (var pdfWriter = new PdfWriter(outputStream);
             var pdfDocument = new com.itextpdf.kernel.pdf.PdfDocument(pdfWriter);
             var document = new Document(pdfDocument)) {

            document.add(new Paragraph(title).setBold().setFontSize(16));
            document.add(table);

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }

        return outputStream.toByteArray();
    }

    public byte[] generateAllSessionsReportPdf(List<ReportSession> reports) {
        Table table = createSessionReportTable();
        reports.forEach(report -> addSessionReportRow(table, report));
        return generatePdf("Reporte General de Sesiones", table);
    }


    public byte[] generatePatientReportPdf(List<ReportSession> reports) {
        Table table = createSessionReportTable();
        reports.forEach(report -> addSessionReportRow(table, report));
        return generatePdf("Reporte de Sesiones por Paciente", table);
    }

    private static final float[] THERAPIST_REPORT_COLUMNS = new float[]{2, 1.5f, 2, 2, 1.5f, 1, 1, 1, 2};

    private Table createTherapistReportTable() {
        Table table = new Table(THERAPIST_REPORT_COLUMNS);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table.setWidth(UnitValue.createPercentValue(95));

        addCenteredHeaderCell(table, "Fecha");
        addCenteredHeaderCell(table, "Hora Inicio");
        addCenteredHeaderCell(table, "Paciente");
        addCenteredHeaderCell(table, "Sesiones/Mes");
        addCenteredHeaderCell(table, "Sala");
        addCenteredHeaderCell(table, "Asist. Paciente");
        addCenteredHeaderCell(table, "Asist. Terapeuta");
        addCenteredHeaderCell(table, "Reprogramada");
        addCenteredHeaderCell(table, "Razón");

        return table;
    }

    private void addCenteredHeaderCell(Table table, String text) {
        Cell cell = new Cell().add(new Paragraph(text));
        cell.setTextAlignment(TextAlignment.CENTER);
        cell.setBold();
        table.addHeaderCell(cell);
    }

    private void addTherapistReportRow(Table table, ReportSession report, int numSessions) {
        addCenteredCell(table, report.sessionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        addCenteredCell(table, report.startTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
        addCenteredCell(table, report.patientName());
        addCenteredCell(table, String.valueOf(numSessions * 4));
        addCenteredCell(table, report.roomName());
        addCenteredCell(table, report.patientPresent() ? "Sí" : "No");
        addCenteredCell(table, report.therapistPresent() ? "Sí" : "No");
        addCenteredCell(table, report.rescheduled() ? "Sí" : "No");
        addCenteredCell(table, report.reason() != null ? report.reason() : "N/A");
    }

    private void addCenteredCell(Table table, String text) {
        Cell cell = new Cell().add(new Paragraph(text));
        cell.setTextAlignment(TextAlignment.CENTER);
        table.addCell(cell);
    }

    public byte[] generateTherapistReportPdf(List<ReportSession> reports, Map<Long, Integer> planSessions) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (var pdfWriter = new PdfWriter(outputStream);
             var pdfDocument = new com.itextpdf.kernel.pdf.PdfDocument(pdfWriter);
             var document = new Document(pdfDocument, PageSize.A4.rotate())) {

            document.setMargins(20, 20, 20, 20);

            Table headerTable = new Table(2);
            headerTable.setWidth(UnitValue.createPercentValue(100));

            Cell logoCell = new Cell();
            logoCell.setBorder(null);
            try {
                Resource resource = resourceLoader.getResource("classpath:static/plenaLOGO-back.jpg");
                Image logo = new Image(ImageDataFactory.create(resource.getInputStream().readAllBytes()));
                logo.setWidth(40);
                logo.setAutoScaleHeight(true);
                logoCell.add(logo);
                logoCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
            } catch (Exception e) {
                System.err.println("Error al cargar el logo: " + e.getMessage());
            }
            headerTable.addCell(logoCell);

            Cell titleCell = new Cell();
            titleCell.setBorder(null);
            String therapistName = reports.isEmpty() ? "No disponible" : reports.get(0).therapistName();
            titleCell.add(new Paragraph(String.format("Reporte de Sesiones - Terapeuta: %s", therapistName))
                    .setBold()
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.LEFT));
            titleCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
            headerTable.addCell(titleCell);

            document.add(headerTable);
            document.add(new Paragraph("\n"));

            Table table = createTherapistReportTable();
            reports.forEach(report ->
                    addTherapistReportRow(table, report,
                            planSessions.getOrDefault(report.planId(), 0))
            );

            document.add(table);


            document.add(table);
            document.add(new Paragraph("\n"));

            Table summaryTable = new Table(2);
            summaryTable.setWidth(UnitValue.createPercentValue(100));

            // Columna izquierda - Resumen
            Cell leftCell = new Cell();
            leftCell.setBorder(null);
            leftCell.add(new Paragraph("Resumen:").setBold().setFontSize(12));
            leftCell.add(new Paragraph(String.format("Total de sesiones: %d", reports.size())));
            leftCell.add(new Paragraph(String.format("Sesiones reprogramadas: %d",
                    reports.stream().filter(ReportSession::rescheduled).count())));
            leftCell.setTextAlignment(TextAlignment.LEFT);
            summaryTable.addCell(leftCell);

            // Columna derecha - Total de Asistencias
            Cell rightCell = new Cell();
            rightCell.setBorder(null);
            rightCell.add(new Paragraph("Asistencias:").setBold().setFontSize(12));
            rightCell.add(new Paragraph(String.format("Total: %d",
                    reports.stream().filter(ReportSession::therapistPresent).count())));
            rightCell.setTextAlignment(TextAlignment.RIGHT);
            summaryTable.addCell(rightCell);

            document.add(summaryTable);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF del reporte de terapeuta", e);
        }

        return outputStream.toByteArray();
    }

    private void addCenteredParagraph(Document document, String text) {
        document.add(new Paragraph(text).setTextAlignment(TextAlignment.CENTER));
    }
}
