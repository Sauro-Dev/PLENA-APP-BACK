package com.plenamente.sgt.domain.dto.PlanDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreatePlanDto(
        @NotNull @Min(1) @Max(6) Integer numOfSessions
){
}
