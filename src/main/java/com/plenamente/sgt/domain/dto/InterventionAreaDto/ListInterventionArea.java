package com.plenamente.sgt.domain.dto.InterventionAreaDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ListInterventionArea(
        @NotNull Long id,
        @NotBlank String name,
        @NotBlank String description
){
}
