package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.ReportDto.ReportDto;
import com.plenamente.sgt.domain.entity.Report;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReportService {
    ReportDto uploadReport(Long medicalHistoryId, MultipartFile file, ReportDto dto);
    ReportDto getReport(Long reportId);
    List<ReportDto> getReportsByMedicalHistory(Long medicalHistoryId);
    void deleteReport(Long reportId);
}
