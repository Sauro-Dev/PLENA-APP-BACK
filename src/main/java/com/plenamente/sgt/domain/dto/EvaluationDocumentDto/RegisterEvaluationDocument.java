package com.plenamente.sgt.domain.dto.EvaluationDocumentDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record RegisterEvaluationDocument(
        @NotNull Long idMedicalHistory,
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String documentType,
        @NotNull MultipartFile archive
){
}
