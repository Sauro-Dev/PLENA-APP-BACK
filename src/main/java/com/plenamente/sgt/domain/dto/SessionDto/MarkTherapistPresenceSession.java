package com.plenamente.sgt.domain.dto.SessionDto;

import jakarta.validation.constraints.NotNull;

public record MarkTherapistPresenceSession(
        @NotNull Long sessionId,
        boolean therapistPresent
) {
}
