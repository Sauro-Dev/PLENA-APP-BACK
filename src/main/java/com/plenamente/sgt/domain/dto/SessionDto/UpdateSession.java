package com.plenamente.sgt.domain.dto.SessionDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateSession(
        @NotNull Long idSession,
        @NotNull LocalDate sessionDate,
        @NotNull LocalTime startTime,
        @NotNull Long therapistId,
        @NotNull Long roomId,
        @NotBlank String reason
) {
}