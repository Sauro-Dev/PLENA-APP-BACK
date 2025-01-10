package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.ReportDto.ReportDto;
import com.plenamente.sgt.domain.entity.MedicalHistory;
import com.plenamente.sgt.domain.entity.Report;
import com.plenamente.sgt.infra.exception.ResourceNotFoundException;
import com.plenamente.sgt.infra.repository.MedicalHistoryRepository;
import com.plenamente.sgt.infra.repository.ReportRepository;
import com.plenamente.sgt.service.ReportService;
import com.plenamente.sgt.service.StorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final StorageService storageService;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public ReportDto uploadReport(Long medicalHistoryId, MultipartFile file, ReportDto dto) {
        MedicalHistory medicalHistory = medicalHistoryRepository.findById(medicalHistoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Medical History not found"));

        String directory = String.format("medical-history/%d/reports", medicalHistoryId);
        String storedFilename = storageService.store(file, directory);
        String fileUrl = storageService.getFileUrl(storedFilename);

        Report report = new Report();
        report.setMedicalHistory(medicalHistory);
        report.setName(dto.getName());
        report.setDescription(dto.getDescription());
        report.setFileUrl(fileUrl);
        report.setFileName(file.getOriginalFilename());
        report.setContentType(file.getContentType());
        report.setFileSize(file.getSize());
        report.setUploadAt(LocalDateTime.now());
        report.setReportPeriodStart(dto.getReportPeriodStart());
        report.setReportPeriodEnd(dto.getReportPeriodEnd());
        report.setReportType("TRIMESTRAL");

        report = reportRepository.save(report);
        return mapToDto(report);
    }

    @Override
    public List<ReportDto> getReportsByMedicalHistory(Long medicalHistoryId) {
        return reportRepository.findByMedicalHistoryIdMedicalHistory(medicalHistoryId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ReportDto mapToDto(Report report) {
        ReportDto dto = modelMapper.map(report, ReportDto.class);
        dto.setMedicalHistoryId(report.getMedicalHistory().getIdMedicalHistory());
        return dto;
    }

    @Override
    public ReportDto getReport(Long reportId) {
        return reportRepository.findById(reportId)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));
    }

    @Override
    public void deleteReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));

        try {
            storageService.delete(report.getFileUrl());
        } catch (Exception e) {
            log.error("Error deleting file: {}", report.getFileUrl(), e);
        }

        reportRepository.delete(report);
    }

}
