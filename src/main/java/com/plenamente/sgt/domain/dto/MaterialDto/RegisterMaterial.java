package com.plenamente.sgt.domain.dto.MaterialDto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.plenamente.sgt.domain.entity.MaterialStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record RegisterMaterial(
        @NotNull String idMaterial,
        @NotNull @Size(min = 1, max = 100) String nombre,
        String descripcion,
        @Min(0) int stock,
        boolean esCompleto,
        boolean esSoporte,
        @NotNull MaterialStatus estado
) {
}