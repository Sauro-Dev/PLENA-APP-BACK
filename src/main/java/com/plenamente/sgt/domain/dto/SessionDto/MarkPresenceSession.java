package com.plenamente.sgt.domain.dto.SessionDto;

import jakarta.validation.constraints.NotNull;

public record MarkPresenceSession(
        @NotNull Long sessionId,
        boolean therapistPresent,
        boolean patientPresent
) {}

