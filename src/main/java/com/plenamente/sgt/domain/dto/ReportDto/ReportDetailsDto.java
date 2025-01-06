package com.plenamente.sgt.domain.dto.ReportDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportDetailsDto(
        @NotNull Long id,
        @NotNull Long idMedicalHistory,
        @NotBlank String name,
        @NotBlank String description
) {
}