package com.plenamente.sgt.domain.dto.SessionDto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ListSession(
        Long idSession,
        LocalDate sessionDate,
        LocalTime startTime,
        LocalTime endTime,
        String patientName,
        String therapistName,
        String roomName,
        boolean rescheduled,
        boolean therapistPresent,
        boolean patientPresent
) {}
