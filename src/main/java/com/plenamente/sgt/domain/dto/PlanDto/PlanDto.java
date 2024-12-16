package com.plenamente.sgt.domain.dto.PlanDto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlanDto (
        @NotNull Integer numOfSessions
){
}
