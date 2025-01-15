package com.plenamente.sgt.domain.dto.SessionDto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReportSession(
        Long idSession,
        LocalDate sessionDate,
        LocalTime startTime,
        LocalTime endTime,
        boolean therapistPresent,
        boolean patientPresent,
        String therapistName,
        String patientName,
        String roomName,
        int renewPlan,
        String reason,
        Long patientId,
        Long planId,
        Long roomId,
        Long therapistId,
        boolean rescheduled
) {
}
