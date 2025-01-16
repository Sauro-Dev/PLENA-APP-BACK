package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.ReportDto.ReportDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReportService {
    ReportDto uploadReport(Long patientId, Long medicalHistoryId, MultipartFile file);
    ReportDto getReport(Long reportId);
    void deleteReport(Long reportId);
    List<ReportDto> getReportsByMedicalHistory(Long medicalHistoryId);
    boolean canUploadReport(Long patientId, Long medicalHistoryId);
}