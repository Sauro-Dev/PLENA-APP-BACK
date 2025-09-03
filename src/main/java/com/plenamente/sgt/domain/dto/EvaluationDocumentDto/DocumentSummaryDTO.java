package com.plenamente.sgt.domain.dto.EvaluationDocumentDto;

import java.time.LocalDateTime;

public record DocumentSummaryDTO(
        String name,
        String description,
        String documentType,
        String fileUrl,
        LocalDateTime uploadAt
) {}
