package com.plenamente.sgt.domain.dto.MedicalHistoryDto;

import com.plenamente.sgt.domain.dto.ReportDto.ReportSummaryDTO;

public record ListMedicalHistory(
        Long id,
        ReportSummaryDTO report,
        String documentName,
        String description,
        String contentType,
        String name
) {
}