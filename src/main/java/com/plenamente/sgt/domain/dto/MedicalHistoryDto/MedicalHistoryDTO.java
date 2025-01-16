package com.plenamente.sgt.domain.dto.MedicalHistoryDto;

import com.plenamente.sgt.domain.dto.EvaluationDocumentDto.DocumentSummaryDTO;
import com.plenamente.sgt.domain.dto.ReportDto.ReportSummaryDTO;

public record MedicalHistoryDTO(
        Long id,
        ReportSummaryDTO report,
        DocumentSummaryDTO document,
        String patientName
) {}