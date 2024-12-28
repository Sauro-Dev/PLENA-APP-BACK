package com.plenamente.sgt.domain.dto.SessionDto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateSession(
        @NotNull Long idSession,
        @NotNull LocalDate sessionDate,
        @NotNull LocalTime startTime,
        String reason
) {}