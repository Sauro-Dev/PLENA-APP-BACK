package com.plenamente.sgt.domain.dto.InterventionAreaDto;

import jakarta.validation.constraints.NotBlank;

public record UpdateInterventionArea(
        @NotBlank String name,
        String description,
        boolean enabled
) {
}
