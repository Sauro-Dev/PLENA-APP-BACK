package com.plenamente.sgt.domain.dto.PatientDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record RenewPlanDto(
        @NotNull(message = "El ID del paciente es obligatorio")
        Long patientId,

        @NotNull(message = "El ID del nuevo plan es obligatorio")
        Long newPlanId,

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime startTime,

        @NotNull(message = "Las fechas de las sesiones iniciales son obligatorias")
        @Size(min = 1, message = "Debe proporcionar al menos una fecha para la primera semana")
        List<LocalDate> firstWeekDates,

        @NotNull(message = "El ID del terapeuta es obligatorio")
        Long therapistId,

        @NotNull(message = "El ID de la sala es obligatorio")
        Long roomId
) {
}
