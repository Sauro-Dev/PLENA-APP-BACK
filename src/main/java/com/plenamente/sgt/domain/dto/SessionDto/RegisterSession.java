package com.plenamente.sgt.domain.dto.SessionDto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record RegisterSession(
        LocalTime startTime,
        Long patientId,
        Long therapistId,
        Long roomId,
        List<LocalDate> firstWeekDates,
        int renewPlan // Nuevo atributo
) {}
