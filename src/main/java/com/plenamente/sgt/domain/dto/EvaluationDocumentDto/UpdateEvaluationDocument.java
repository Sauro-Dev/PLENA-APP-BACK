package com.plenamente.sgt.domain.dto.EvaluationDocumentDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateEvaluationDocument (
        @NotNull Long idMedicalHistory,
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String documentType
){

}
