package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.ReportDto.RegisterReport;
import com.plenamente.sgt.domain.dto.ReportDto.ReportDetailsDto;
import com.plenamente.sgt.domain.dto.ReportDto.UpdateReport;
import com.plenamente.sgt.domain.entity.Report;

import java.util.List;

public interface ReportService {
    Report createReport(RegisterReport report);
    Report updateReport(Long idReport, UpdateReport ReportUp);
    ReportDetailsDto findReportById(Long id);
    List<ReportDetailsDto> findReportsByMedicalHistoryId(Long idMedicalHistory);
}
