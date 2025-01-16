package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.ReportDto.ReportDto;
import com.plenamente.sgt.infra.exception.StorageFileNotFoundException;
import com.plenamente.sgt.service.ReportService;
import com.plenamente.sgt.service.StorageService;
import com.plenamente.sgt.service.TreatmentService;
import com.plenamente.sgt.service.storage.StorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
@Slf4j
public class ReportController {
    private final ReportService reportService;
    private final TreatmentService treatmentService;
    private final StorageService storageService;
    private final StorageProperties storageProperties;

    @PostMapping("/medical-history/{medicalHistoryId}/patient/{patientId}/upload")
    public ResponseEntity<?> uploadReport(
            @PathVariable Long medicalHistoryId,
            @PathVariable Long patientId,
            @RequestParam("file") MultipartFile file) {
        try {
            ReportDto report = reportService.uploadReport(patientId, medicalHistoryId, file);
            return ResponseEntity.ok(report);
        } catch (IllegalStateException e) {
            log.warn("Error de validación al subir reporte: {}", e.getMessage());
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
            return ResponseEntity.badRequest().body(problemDetail);
        } catch (Exception e) {
            log.error("Error al subir reporte", e);
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al procesar el archivo: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(problemDetail);
        }
    }


    @GetMapping("/medical-history/{medicalHistoryId}/patient/{patientId}/can-upload")
    public ResponseEntity<?> canUploadReport(
            @PathVariable Long medicalHistoryId,
            @PathVariable Long patientId) {
        try {
            boolean canUpload = reportService.canUploadReport(patientId, medicalHistoryId);
            Map<String, Object> response = new HashMap<>();
            response.put("canUpload", canUpload);

            if (!canUpload) {
                int treatmentMonths = this.treatmentService.calculateTreatmentMonths(patientId);
                if (treatmentMonths < 3) {
                    response.put("message", "Debe esperar " + (3 - treatmentMonths) + " meses más para subir el primer reporte");
                    response.put("nextAvailableDate", LocalDate.now().plusMonths(3 - treatmentMonths));
                } else {
                    response.put("message", "Ya existe el máximo de reportes permitidos o debe esperar más tiempo");
                }
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al verificar disponibilidad de reporte", e);
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al verificar disponibilidad: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(problemDetail);
        }
    }

    @GetMapping("/medical-history/{medicalHistoryId}")
    public ResponseEntity<List<ReportDto>> getReportsByMedicalHistory(
            @PathVariable Long medicalHistoryId) {
        return ResponseEntity.ok(reportService.getReportsByMedicalHistory(medicalHistoryId));
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDto> getReport(@PathVariable Long reportId) {
        return ResponseEntity.ok(reportService.getReport(reportId));
    }

    @GetMapping("/medical-history/{medicalHistoryId}/document/{reportId}/download")
    public ResponseEntity<Resource> downloadReport(
            @PathVariable Long medicalHistoryId,
            @PathVariable Long reportId) {
        try {
            ReportDto report = reportService.getReport(reportId);

            if (!report.getMedicalHistoryId().equals(medicalHistoryId)) {
                throw new IllegalArgumentException("El reporte no pertenece al historial médico especificado");
            }

            String relativePath = report.getFileUrl()
                    .replace(storageProperties.getServerUrl() + "/static/", "");

            Resource file = storageService.loadAsResource(relativePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(report.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + report.getFileName() + "\"")
                    .body(file);
        } catch (Exception e) {
            throw new StorageFileNotFoundException("No se pudo descargar el archivo: " + reportId);
        }
    }
}