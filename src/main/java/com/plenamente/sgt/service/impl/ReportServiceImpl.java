package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.ReportDto.ReportDto;
import com.plenamente.sgt.domain.entity.MedicalHistory;
import com.plenamente.sgt.domain.entity.Report;
import com.plenamente.sgt.infra.exception.*;
import com.plenamente.sgt.infra.repository.MedicalHistoryRepository;
import com.plenamente.sgt.infra.repository.PatientRepository;
import com.plenamente.sgt.infra.repository.ReportRepository;
import com.plenamente.sgt.service.ReportService;
import com.plenamente.sgt.service.StorageService;
import com.plenamente.sgt.service.TreatmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = "reports")
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final PatientRepository patientRepository;
    private final StorageService storageService;
    private final TreatmentService treatmentService;
    private final ModelMapper modelMapper;

    @CacheEvict(cacheNames = "reports", key = "'medical_history_' + #medicalHistoryId")
    @Override
    public ReportDto uploadReport(Long patientId, Long medicalHistoryId, MultipartFile file) {
        try {
            if (!treatmentService.canUploadReport(patientId, medicalHistoryId)) {
                throw new IllegalArgumentException("No se puede subir el reporte en este momento.");
            }

            MedicalHistory medicalHistory = validateAndGetMedicalHistory(patientId, medicalHistoryId);
            int treatmentMonth = treatmentService.calculateTreatmentMonths(patientId);

            String directory = String.format("patients/%d/medical-history/%d/reports",
                    patientId, medicalHistoryId);

            String storedFilename = this.storageService.store(file, directory);
            String fileUrl = this.storageService.getFileUrl(storedFilename);

            Report report = new Report();
            report.setMedicalHistory(medicalHistory);
            report.setName(String.format("Reporte Mes %d", treatmentMonth));
            report.setDescription("Reporte de progreso del tratamiento");
            report.setFileUrl(fileUrl);
            report.setFileName(file.getOriginalFilename());
            report.setContentType(file.getContentType());
            report.setFileSize(file.getSize());
            report.setUploadAt(LocalDateTime.now());
            report.setTreatmentMonth(treatmentMonth);

            report = reportRepository.save(report);
            return modelMapper.map(report, ReportDto.class);

        } catch (StorageException e) {
            throw new FileStorageException("Error al almacenar el archivo: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ServiceException("Error al procesar el reporte", e);
        }
    }

    @Override
    @Cacheable(key = "#reportId", unless = "#result == null")
    @Transactional(readOnly = true)
    public ReportDto getReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con id: " + reportId));

        ReportDto dto = modelMapper.map(report, ReportDto.class);
        dto.setMedicalHistoryId(report.getMedicalHistory().getIdMedicalHistory());
        return dto;
    }


    @Override
    @CacheEvict(cacheNames = "reports", allEntries = true)
    public void deleteReport(Long reportId) {
        try {
            Report report = reportRepository.findById(reportId)
                    .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con id: " + reportId));

            this.storageService.delete(report.getFileUrl());
            reportRepository.delete(report);
            log.debug("Reporte eliminado exitosamente: {}", reportId);

        } catch (StorageFileNotFoundException e) {
            log.warn("Archivo no encontrado al eliminar reporte: {}", e.getMessage());
            reportRepository.deleteById(reportId);
        } catch (StorageException e) {
            log.error("Error al eliminar el archivo: {}", e.getMessage());
            throw new FileStorageException("Error al eliminar el archivo", e);
        }
    }

    @Override
    @Cacheable(key = "'medical_history_' + #medicalHistoryId", unless = "#result.empty")
    @Transactional(readOnly = true)
    public List<ReportDto> getReportsByMedicalHistory(Long medicalHistoryId) {
        List<Report> reports = reportRepository.findByMedicalHistoryIdMedicalHistory(medicalHistoryId);
        return reports.stream()
                .map(report -> modelMapper.map(report, ReportDto.class))
                .collect(Collectors.toList());
    }

    private MedicalHistory validateAndGetMedicalHistory(Long patientId, Long medicalHistoryId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + patientId));

        MedicalHistory medicalHistory = medicalHistoryRepository.findById(medicalHistoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Historia médica no encontrada con id: " + medicalHistoryId));

        if (!medicalHistory.getPatient().getIdPatient().equals(patientId)) {
            throw new IllegalArgumentException("La historia médica no pertenece al paciente especificado");
        }

        return medicalHistory;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean canUploadReport(Long patientId, Long medicalHistoryId) {
        try {
            int treatmentMonths = treatmentService.calculateTreatmentMonths(patientId);

            if (treatmentMonths < 3) {
                return false;
            }

            List<Report> existingReports = reportRepository
                    .findByMedicalHistoryIdMedicalHistory(medicalHistoryId);

            if (existingReports.isEmpty()) {
                return true;
            }

            if (existingReports.size() >= 2) {
                return false;
            }

            Report lastReport = existingReports.get(0);
            long monthsSinceLastReport = ChronoUnit.MONTHS.between(
                    YearMonth.from(lastReport.getUploadAt()),
                    YearMonth.from(LocalDateTime.now())
            );

            return monthsSinceLastReport >= 3;
        } catch (Exception e) {
            log.error("Error al verificar si se puede subir reporte", e);
            return false;
        }
    }
}