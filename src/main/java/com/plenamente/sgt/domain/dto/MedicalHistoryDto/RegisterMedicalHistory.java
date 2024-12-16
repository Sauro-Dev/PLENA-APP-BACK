package com.plenamente.sgt.domain.dto.MedicalHistoryDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterMedicalHistory(
        @NotNull Long idPatient,
        @NotBlank String name
) {
}
