package com.plenamente.sgt.domain.dto.SessionDto;

import jakarta.validation.constraints.NotNull;

public record MarkPatientPresenceSession(
        @NotNull Long sessionId,
        boolean patientPresent
) {
}

