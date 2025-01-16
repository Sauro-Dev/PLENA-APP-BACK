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
import static com.plenamente.sgt.infra.config.ReportConstants.BATCH_SIZE;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PdfGenerationService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private static final String LOGO_PATH = "classpath:static/plenaLOGO-back.jpg";
    private static final float LOGO_WIDTH = 40f;
    private final ResourceLoader resourceLoader;
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

    private Document initializeDocument(ByteArrayOutputStream outputStream) {
        var pdfWriter = new PdfWriter(outputStream);
        var pdfDocument = new com.itextpdf.kernel.pdf.PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument, PageSize.A4.rotate());
        document.setMargins(20, 20, 20, 20);
        return document;
    }

    private void addReportRow(Table table, ReportSession report, int numSessions, boolean isPatientReport) {
        addCenteredCell(table, report.sessionDate().format(DATE_FORMATTER));
        addCenteredCell(table, report.startTime().format(TIME_FORMATTER));
        addCenteredCell(table, isPatientReport ? report.therapistName() : report.patientName());
        addCenteredCell(table, String.valueOf(numSessions * 4));
        addCenteredCell(table, report.roomName());
        addCenteredCell(table, report.patientPresent() ? "Sí" : "No");
        addCenteredCell(table, report.therapistPresent() ? "Sí" : "No");
        addCenteredCell(table, report.rescheduled() ? "Sí" : "No");
        addCenteredCell(table, report.reason() != null ? report.reason() : "N/A");
    }

    private Table createReportTable(boolean isPatientReport) {
        Table table = new Table(DETAILED_REPORT_COLUMNS);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        table.setWidth(UnitValue.createPercentValue(95));

        addCenteredHeaderCell(table, "Fecha");
        addCenteredHeaderCell(table, "Hora Inicio");
        addCenteredHeaderCell(table, isPatientReport ? "Terapeuta" : "Paciente");
        addCenteredHeaderCell(table, "Sesiones/Mes");
        addCenteredHeaderCell(table, "Sala");
        addCenteredHeaderCell(table, "Asist. Paciente");
        addCenteredHeaderCell(table, "Asist. Terapeuta");
        addCenteredHeaderCell(table, "Reprogramada");
        addCenteredHeaderCell(table, "Razón");

        return table;
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

    private Table createGeneralSummaryTable(int totalSessions, long totalRescheduled,
                                            long totalTherapistPresent, long totalPatientPresent) {
        Table generalSummary = new Table(2);
        generalSummary.setWidth(UnitValue.createPercentValue(100));

        Cell leftCell = new Cell();
        leftCell.setBorder(null);
        leftCell.add(new Paragraph("Total General:").setBold().setFontSize(12));
        leftCell.add(new Paragraph(String.format("Total de sesiones: %d", totalSessions)));
        leftCell.add(new Paragraph(String.format("Sesiones reprogramadas: %d", totalRescheduled)));
        leftCell.setTextAlignment(TextAlignment.LEFT);
        generalSummary.addCell(leftCell);

        Cell rightCell = new Cell();
        rightCell.setBorder(null);
        rightCell.add(new Paragraph("Asistencias Totales:").setBold().setFontSize(12));
        rightCell.add(new Paragraph(String.format("Terapeutas: %d", totalTherapistPresent)));
        rightCell.add(new Paragraph(String.format("Pacientes: %d", totalPatientPresent)));
        rightCell.setTextAlignment(TextAlignment.RIGHT);
        generalSummary.addCell(rightCell);

        return generalSummary;
    }

    private void processTherapistSection(Document document, Long therapistId,
                                         List<ReportSession> therapistSessions,
                                         Map<Long, Integer> planSessions) {
        try {
            String therapistName = therapistSessions.get(0).therapistName();
            document.add(new Paragraph(String.format("Terapeuta: %s", therapistName))
                    .setBold()
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph("\n"));

            Table therapistTable = createReportTable(false);

            for (int i = 0; i < therapistSessions.size(); i += BATCH_SIZE) {
                int endIndex = Math.min(i + BATCH_SIZE, therapistSessions.size());
                List<ReportSession> sessionBatch = therapistSessions.subList(i, endIndex);

                sessionBatch.forEach(session ->
                        addReportRow(therapistTable, session,
                                planSessions.getOrDefault(session.planId(), 0), false));
            }

            document.add(therapistTable);
            document.add(createSummaryTable(therapistSessions, false));
            document.add(new Paragraph("\n\n"));

        } catch (Exception e) {
            System.err.println("Error al procesar sección del terapeuta " + therapistId +
                    ": " + e.getMessage());
        }
    }

    private byte[] generateDetailedReportPdf(String title, List<ReportSession> reports,
                                             Map<Long, Integer> planSessions, boolean isPatientReport) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (Document document = initializeDocument(outputStream)) {
            document.add(createHeaderWithLogo(title));
            document.add(new Paragraph("\n"));

            Table table = createReportTable(isPatientReport);
            reports.forEach(report -> {
                int numSessions = planSessions.getOrDefault(report.planId(), 0);
                addReportRow(table, report, numSessions, isPatientReport);
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

    private void addGeneralSummary(Document document, List<ReportSession> reports) {
        document.add(new Paragraph("Resumen General")
                .setBold()
                .setFontSize(14)
                .setTextAlignment(TextAlignment.LEFT));

        // Calcular estadísticas en lotes para optimizar memoria
        AtomicLong totalRescheduled = new AtomicLong(0);
        AtomicLong totalTherapistPresent = new AtomicLong(0);
        AtomicLong totalPatientPresent = new AtomicLong(0);

        for (int i = 0; i < reports.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, reports.size());
            List<ReportSession> batch = reports.subList(i, endIndex);

            totalRescheduled.addAndGet(batch.stream().filter(ReportSession::rescheduled).count());
            totalTherapistPresent.addAndGet(batch.stream().filter(ReportSession::therapistPresent).count());
            totalPatientPresent.addAndGet(batch.stream().filter(ReportSession::patientPresent).count());
        }

        Table generalSummary = createGeneralSummaryTable(reports.size(),
                totalRescheduled.get(), totalTherapistPresent.get(), totalPatientPresent.get());
        document.add(generalSummary);
    }

    // Métodos públicos
    public byte[] generateAllSessionsReportPdf(List<ReportSession> reports,
                                               Map<Long, Integer> planSessions) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (Document document = initializeDocument(outputStream)) {
            document.add(createHeaderWithLogo("Reporte General de Sesiones"));
            document.add(new Paragraph("\n"));

            Map<Long, List<ReportSession>> sessionsByTherapist = reports.stream()
                    .collect(Collectors.groupingBy(ReportSession::therapistId));

            List<Long> therapistIds = new ArrayList<>(sessionsByTherapist.keySet());

            for (int i = 0; i < therapistIds.size(); i += BATCH_SIZE) {
                int endIndex = Math.min(i + BATCH_SIZE, therapistIds.size());
                List<Long> therapistBatch = therapistIds.subList(i, endIndex);

                for (Long therapistId : therapistBatch) {
                    List<ReportSession> therapistSessions = sessionsByTherapist.get(therapistId);
                    processTherapistSection(document, therapistId, therapistSessions, planSessions);
                }
                document.flush();
            }

            addGeneralSummary(document, reports);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF del reporte general", e);
        }

        return outputStream.toByteArray();
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