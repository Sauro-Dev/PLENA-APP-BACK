package com.plenamente.sgt.domain.dto.MaterialDto;

import com.plenamente.sgt.domain.entity.MaterialStatus;

import java.time.LocalDateTime;

public record ListMaterial(
        String idMaterial,
        String nombre,
        String descripcion,
        int stock,
        MaterialStatus estado,
        LocalDateTime fechaAlta,
        boolean esCompleto,
        boolean esSoporte,
        Long idRoom
) {
}
