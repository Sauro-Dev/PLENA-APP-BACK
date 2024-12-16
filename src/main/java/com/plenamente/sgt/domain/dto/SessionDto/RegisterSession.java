package com.plenamente.sgt.domain.dto.SessionDto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record RegisterSession(
        @NotNull LocalDate sessionDate,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        @NotNull Long patientId,
        @NotNull Long therapistId,
        @NotNull Long roomId,
        @NotNull Long planId
) {}
