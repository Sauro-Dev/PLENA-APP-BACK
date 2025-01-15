package com.plenamente.sgt.service.impl;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.plenamente.sgt.domain.dto.SessionDto.ReportSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PdfGenerationService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private static final String LOGO_PATH = "classpath:static/plenaLOGO-back.jpg";
    private static final float LOGO_WIDTH = 40f;

    private final ResourceLoader resourceLoader;

    // Constantes para las columnas de las tablas
    private static final float[] SESSION_REPORT_COLUMNS = new float[]{1, 2, 2, 2, 1, 1, 2, 2, 1, 1, 1, 1, 1};
    private static final float[] DETAILED_REPORT_COLUMNS = new float[]{2, 1.5f, 2, 2, 1.5f, 1, 1, 1, 2};

    private Table createHeaderWithLogo(String title) {
        Table headerTable = new Table(2);
        headerTable.setWidth(UnitValue.createPercentValue(100));

        Cell logoCell = createLogoCell();
        Cell titleCell = createTitleCell(title);

        headerTable.addCell(logoCell);
        headerTable.addCell(titleCell);

        return headerTable;
    }

    private Cell createLogoCell() {
        Cell logoCell = new Cell();
        logoCell.setBorder(null);
        try {
            Resource resource = resourceLoader.getResource(LOGO_PATH);
            Image logo = new Image(ImageDataFactory.create(resource.getInputStream().readAllBytes()));
            logo.setWidth(LOGO_WIDTH);
            logo.setAutoScaleHeight(true);
            logoCell.add(logo);
            logoCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        } catch (Exception e) {
            System.err.println("Error al cargar el logo: " + e.getMessage());
        }
        return logoCell;
    }

    private Cell createTitleCell(String title) {
        Cell titleCell = new Cell();
        titleCell.setBorder(null);
        titleCell.add(new Paragraph(title)
                .setBold()
                .setFontSize(16)
                .setTextAlignment(TextAlignment.LEFT));
        titleCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        return titleCell;
    }

    private void addCenteredHeaderCell(Table table, String text) {
        Cell cell = new Cell()
                .add(new Paragraph(text))
                .setTextAlignment(TextAlignment.CENTER)
                .setBold();
        table.addHeaderCell(cell);
    }

    private void addCenteredCell(Table table, String text) {
        Cell cell = new Cell()
                .add(new Paragraph(text))
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(cell);
    }

    private Document initializeDocument(ByteArrayOutputStream outputStream, boolean rotate) {
        var pdfWriter = new PdfWriter(outputStream);
        var pdfDocument = new com.itextpdf.kernel.pdf.PdfDocument(pdfWriter);
        Document document = rotate ?
                new Document(pdfDocument, PageSize.A4.rotate()) :
                new Document(pdfDocument);

        if (rotate) {
            document.setMargins(20, 20, 20, 20);
        }
        return document;
    }

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
        table.addCell(report.sessionDate().format(DATE_FORMATTER));
        table.addCell(report.startTime().format(TIME_FORMATTER));
        table.addCell(report.endTime().format(TIME_FORMATTER));
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

    private Table createTherapistReportTable() {
        Table table = new Table(DETAILED_REPORT_COLUMNS);
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

    private void addTherapistReportRow(Table table, ReportSession report, int numSessions) {
        addCenteredCell(table, report.sessionDate().format(DATE_FORMATTER));
        addCenteredCell(table, report.startTime().format(TIME_FORMATTER));
        addCenteredCell(table, report.patientName());
        addCenteredCell(table, String.valueOf(numSessions * 4));
        addCenteredCell(table, report.roomName());
        addCenteredCell(table, report.patientPresent() ? "Sí" : "No");
        addCenteredCell(table, report.therapistPresent() ? "Sí" : "No");
        addCenteredCell(table, report.rescheduled() ? "Sí" : "No");
        addCenteredCell(table, report.reason() != null ? report.reason() : "N/A");
    }

    private Table createPatientReportTable() {
        Table table = new Table(DETAILED_REPORT_COLUMNS);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table.setWidth(UnitValue.createPercentValue(95));

        addCenteredHeaderCell(table, "Fecha");
        addCenteredHeaderCell(table, "Hora Inicio");
        addCenteredHeaderCell(table, "Terapeuta");
        addCenteredHeaderCell(table, "Sesiones/Mes");
        addCenteredHeaderCell(table, "Sala");
        addCenteredHeaderCell(table, "Asist. Paciente");
        addCenteredHeaderCell(table, "Asist. Terapeuta");
        addCenteredHeaderCell(table, "Reprogramada");
        addCenteredHeaderCell(table, "Razón");

        return table;
    }

    private void addPatientReportRow(Table table, ReportSession report, int numSessions) {
        addCenteredCell(table, report.sessionDate().format(DATE_FORMATTER));
        addCenteredCell(table, report.startTime().format(TIME_FORMATTER));
        addCenteredCell(table, report.therapistName());
        addCenteredCell(table, String.valueOf(numSessions * 4));
        addCenteredCell(table, report.roomName());
        addCenteredCell(table, report.patientPresent() ? "Sí" : "No");
        addCenteredCell(table, report.therapistPresent() ? "Sí" : "No");
        addCenteredCell(table, report.rescheduled() ? "Sí" : "No");
        addCenteredCell(table, report.reason() != null ? report.reason() : "N/A");
    }

    private Table createSummaryTable(List<ReportSession> reports, boolean isPatientReport) {
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

        long attendanceCount = isPatientReport ?
                reports.stream().filter(ReportSession::patientPresent).count() :
                reports.stream().filter(ReportSession::therapistPresent).count();

        rightCell.add(new Paragraph(String.format("Total: %d", attendanceCount)));
        rightCell.setTextAlignment(TextAlignment.RIGHT);
        summaryTable.addCell(rightCell);

        return summaryTable;
    }

    private byte[] generateBasicPdf(String title, Table table) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (Document document = initializeDocument(outputStream, false)) {
            document.add(new Paragraph(title).setBold().setFontSize(16));
            document.add(table);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }
        return outputStream.toByteArray();
    }

    private byte[] generateDetailedReportPdf(String title, List<ReportSession> reports,
                                             Map<Long, Integer> planSessions, boolean isPatientReport) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (Document document = initializeDocument(outputStream, true)) {
            document.add(createHeaderWithLogo(title));
            document.add(new Paragraph("\n"));

            Table table = isPatientReport ? createPatientReportTable() : createTherapistReportTable();
            reports.forEach(report -> {
                int numSessions = planSessions.getOrDefault(report.planId(), 0);
                if (isPatientReport) {
                    addPatientReportRow(table, report, numSessions);
                } else {
                    addTherapistReportRow(table, report, numSessions);
                }
            });

            document.add(table);
            document.add(new Paragraph("\n"));
            document.add(createSummaryTable(reports, isPatientReport));

        } catch (Exception e) {
            String errorMessage = String.format("Error al generar el PDF del reporte de %s",
                    isPatientReport ? "paciente" : "terapeuta");
            throw new RuntimeException(errorMessage, e);
        }
        return outputStream.toByteArray();
    }

    // Métodos públicos
    public byte[] generateAllSessionsReportPdf(List<ReportSession> reports) {
        Table table = createSessionReportTable();
        reports.forEach(report -> addSessionReportRow(table, report));
        return generateBasicPdf("Reporte General de Sesiones", table);
    }

    public byte[] generatePatientReportPdf(List<ReportSession> reports, Map<Long, Integer> planSessions) {
        String patientName = reports.isEmpty() ? "No disponible" : reports.get(0).patientName();
        return generateDetailedReportPdf(
                String.format("Reporte de Sesiones - Paciente: %s", patientName),
                reports,
                planSessions,
                true
        );
    }

    public byte[] generateTherapistReportPdf(List<ReportSession> reports, Map<Long, Integer> planSessions) {
        String therapistName = reports.isEmpty() ? "No disponible" : reports.get(0).therapistName();
        return generateDetailedReportPdf(
                String.format("Reporte de Sesiones - Terapeuta: %s", therapistName),
                reports,
                planSessions,
                false
        );
    }
}