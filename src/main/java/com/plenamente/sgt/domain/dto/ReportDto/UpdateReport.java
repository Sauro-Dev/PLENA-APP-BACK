package com.plenamente.sgt.domain.dto.ReportDto;

import jakarta.validation.constraints.NotBlank;

public record UpdateReport (
        @NotBlank String name,
        @NotBlank String description
){
}
