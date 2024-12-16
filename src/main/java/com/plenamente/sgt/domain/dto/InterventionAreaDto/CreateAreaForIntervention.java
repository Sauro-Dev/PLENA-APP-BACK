package com.plenamente.sgt.domain.dto.InterventionAreaDto;

import jakarta.validation.constraints.NotBlank;

public record CreateAreaForIntervention(
        @NotBlank String name,
        @NotBlank String description
) {
}