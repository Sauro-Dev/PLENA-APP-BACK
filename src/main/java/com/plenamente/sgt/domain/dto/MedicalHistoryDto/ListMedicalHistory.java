package com.plenamente.sgt.domain.dto.MedicalHistoryDto;

import com.plenamente.sgt.domain.entity.EvaluationDocument;
import com.plenamente.sgt.domain.entity.Report;

public record ListMedicalHistory(
        Report report,
        // VARIABLES DEL EVALUATION DOCUMENT
        String documentName,
        String description,
        String documentType,
        byte[] archive,
        String name,
        Long id
) {
}
