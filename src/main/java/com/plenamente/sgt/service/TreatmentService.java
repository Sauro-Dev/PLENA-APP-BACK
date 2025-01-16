package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.entity.Report;
import com.plenamente.sgt.domain.entity.Session;
import com.plenamente.sgt.infra.exception.ResourceNotFoundException;
import com.plenamente.sgt.infra.repository.ReportRepository;
import com.plenamente.sgt.infra.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
    public class TreatmentService {
    private final SessionRepository sessionRepository;
    private final ReportRepository reportRepository;

    public int calculateTreatmentMonths(Long patientId) {
        Session firstSession = sessionRepository.findFirstByPatient_IdPatientOrderBySessionDateAsc(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontraron sesiones para el paciente"));

        LocalDate treatmentStart = firstSession.getSessionDate();
        LocalDate now = LocalDate.now();

        return (int) ChronoUnit.MONTHS.between(
                YearMonth.from(treatmentStart),
                YearMonth.from(now)
        );
    }

    public boolean canUploadReport(Long patientId, Long medicalHistoryId) {
        int treatmentMonths = calculateTreatmentMonths(patientId);

        // Verificar si han pasado al menos 3 meses
        if (treatmentMonths < 3) {
            return false;
        }

        // Obtener cantidad de reportes existentes
        List<Report> existingReports = reportRepository.findByMedicalHistoryIdMedicalHistory(medicalHistoryId);

        // Si no hay reportes, se puede subir
        if (existingReports.isEmpty()) {
            return true;
        }

        // Si ya hay 2 reportes, no se puede subir más
        if (existingReports.size() >= 2) {
            return false;
        }

        // Para el segundo reporte, verificar que hayan pasado 3 meses desde el último
        Report lastReport = existingReports.get(0);
        int monthsSinceLastReport = (int) ChronoUnit.MONTHS.between(
                YearMonth.from(lastReport.getUploadAt()),
                YearMonth.from(LocalDateTime.now())
        );

        return monthsSinceLastReport >= 3;
    }
}