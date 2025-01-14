package com.plenamente.sgt.service.impl;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.plenamente.sgt.domain.dto.SessionDto.AttendanceReport;
import com.plenamente.sgt.domain.dto.SessionDto.ReportSession;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfGenerationService {

    private static final float[] SESSION_REPORT_COLUMNS = new float[]{1, 2, 2, 2, 1, 1, 2, 2, 1, 1, 1, 1, 1};
    private static final float[] ATTENDANCE_REPORT_COLUMNS = new float[]{1, 2, 2, 2, 2};

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

    public byte[] generateAttendanceReportPdf(List<AttendanceReport> reports) {
        Table table = new Table(ATTENDANCE_REPORT_COLUMNS);
        table.addHeaderCell("ID Sesión");
        table.addHeaderCell("Fecha");
        table.addHeaderCell("Terapeuta");
        table.addHeaderCell("Paciente");
        table.addHeaderCell("Presente");

        reports.forEach(report -> {
            table.addCell(String.valueOf(report.idSession()));
            table.addCell(String.valueOf(report.sessionDate()));
            table.addCell(report.therapistName());
            table.addCell(report.patientName());
            table.addCell(report.present() ? "Sí" : "No");
        });

        return generatePdf("Reporte de Asistencias", table);
    }

    public byte[] generateTherapistReportPdf(List<ReportSession> reports) {
        Table table = createSessionReportTable();
        reports.forEach(report -> addSessionReportRow(table, report));
        return generatePdf("Reporte de Sesiones por Terapeuta", table);
    }

    public byte[] generatePatientReportPdf(List<ReportSession> reports) {
        Table table = createSessionReportTable();
        reports.forEach(report -> addSessionReportRow(table, report));
        return generatePdf("Reporte de Sesiones por Paciente", table);
    }
}