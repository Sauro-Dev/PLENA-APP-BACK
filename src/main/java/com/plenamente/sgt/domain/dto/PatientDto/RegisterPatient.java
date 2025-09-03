package com.plenamente.sgt.domain.dto.PatientDto;

import com.plenamente.sgt.domain.entity.Tutor;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record RegisterPatient(
        @NotBlank @Length(min = 2, max = 30) String name,
        @NotBlank @Length(min = 2, max = 30) String paternalSurname,
        @NotNull @Pattern(regexp = "\\d{8}") String dni,
        @NotNull @Length(max = 30) String maternalSurname,
        @Past LocalDate birthdate,
        @Null @Length(max = 255) String presumptiveDiagnosis,
        boolean status,
        @NotNull Long idPlan,
        @NotEmpty @Size(min = 1, max = 2) List<Tutor> tutor,
        @NotNull Long therapistId,
        @NotNull Long roomId,
        @NotNull LocalTime startTime,
        @NotNull List<LocalDate> firstWeekDates
) {
}
