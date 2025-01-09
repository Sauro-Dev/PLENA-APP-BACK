package com.plenamente.sgt.domain.dto.InterventionAreaDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record CreateAreaForIntervention(
        @NotBlank String name,
        @NotNull String description
        ) {
}