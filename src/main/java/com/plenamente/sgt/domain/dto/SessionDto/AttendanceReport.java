package com.plenamente.sgt.domain.dto.SessionDto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AttendanceReport(
        Long idSession,
        LocalDate sessionDate,
        LocalTime startTime,
        LocalTime endTime,
        String therapistName,
        String patientName,
        boolean present
) {
}
