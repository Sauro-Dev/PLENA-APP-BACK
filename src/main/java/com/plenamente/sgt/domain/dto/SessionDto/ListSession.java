package com.plenamente.sgt.domain.dto.SessionDto;

import java.time.LocalDate;

public record ListSession(
        Long idSession,
        Long therapistId,
        LocalDate sessionDate,
        String startTime,
        String endTime,
        String patientName,
        String therapistName,
        String roomName,
        boolean rescheduled,
        boolean therapistPresent,
        boolean patientPresent
) {
}
