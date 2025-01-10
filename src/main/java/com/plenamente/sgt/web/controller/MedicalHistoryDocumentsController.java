package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.EvaluationDocumentDto.EvaluationDocumentDto;
import com.plenamente.sgt.domain.dto.ReportDto.ReportDto;
import com.plenamente.sgt.service.EvaluationDocumentService;
import com.plenamente.sgt.service.PatientService;
import com.plenamente.sgt.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/medical-history")
@RequiredArgsConstructor
@Slf4j
public class MedicalHistoryDocumentsController {
    private final EvaluationDocumentService evaluationService;
    private final ReportService reportService;
    private final PatientService patientService;

    // Endpoints para Documentos de Evaluación
    @PreAuthorize("hasAnyRole('ADMIN', 'THERAPIST')")
    @GetMapping("/{medicalHistoryId}/evaluations")
    public ResponseEntity<List<EvaluationDocumentDto>> getEvaluations(
            @PathVariable Long medicalHistoryId) {
        return ResponseEntity.ok(evaluationService.getDocumentsByMedicalHistory(medicalHistoryId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'THERAPIST')")
    @GetMapping("/{medicalHistoryId}/evaluations/{documentId}")
    public ResponseEntity<EvaluationDocumentDto> getEvaluation(
            @PathVariable Long medicalHistoryId,
            @PathVariable Long documentId) {
        return ResponseEntity.ok(evaluationService.getDocument(documentId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'THERAPIST')")
    @PostMapping("/{medicalHistoryId}/evaluations")
    public ResponseEntity<EvaluationDocumentDto> uploadEvaluation(
            @PathVariable Long patientId,
            @PathVariable Long medicalHistoryId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("evaluationType") String evaluationType) {

        EvaluationDocumentDto dto = new EvaluationDocumentDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setEvaluationType(evaluationType);
        dto.setEvaluationDate(LocalDateTime.now());

        return ResponseEntity.ok(evaluationService.uploadDocument(patientId, medicalHistoryId, file, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'THERAPIST')")
    @DeleteMapping("/{medicalHistoryId}/evaluations/{documentId}")
    public ResponseEntity<Void> deleteEvaluation(
            @PathVariable Long medicalHistoryId,
            @PathVariable Long documentId) {
        evaluationService.deleteDocument(documentId);
        return ResponseEntity.noContent().build();
    }

    // Endpoints para Informes Trimestrales
    @PreAuthorize("hasAnyRole('ADMIN', 'THERAPIST')")
    @GetMapping("/{medicalHistoryId}/reports")
    public ResponseEntity<List<ReportDto>> getReports(
            @PathVariable Long medicalHistoryId) {
        return ResponseEntity.ok(reportService.getReportsByMedicalHistory(medicalHistoryId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'THERAPIST')")
    @GetMapping("/{medicalHistoryId}/reports/{reportId}")
    public ResponseEntity<ReportDto> getReport(
            @PathVariable Long medicalHistoryId,
            @PathVariable Long reportId) {
        return ResponseEntity.ok(reportService.getReport(reportId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'THERAPIST')")
    @PostMapping("/{medicalHistoryId}/reports")
    public ResponseEntity<ReportDto> uploadReport(
            @PathVariable Long medicalHistoryId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("periodStart") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @RequestParam("periodEnd") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd) {

        ReportDto dto = new ReportDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setReportPeriodStart(periodStart.atStartOfDay());
        dto.setReportPeriodEnd(periodEnd.atTime(LocalTime.MAX));

        return ResponseEntity.ok(reportService.uploadReport(medicalHistoryId, file, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'THERAPIST')")
    @DeleteMapping("/{medicalHistoryId}/reports/{reportId}")
    public ResponseEntity<Void> deleteReport(
            @PathVariable Long medicalHistoryId,
            @PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }
}