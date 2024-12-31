package com.plenamente.sgt.domain.dto.PatientDto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDate;

public record UpdatePatient(
        @NotNull Long idPatient,
        @NotBlank @Length(min = 2, max = 30) String name,
        @NotBlank @Length(min = 2, max = 30) String paternalSurname,
        @NotNull @Pattern(regexp = "\\d{8}") String dni,
        @NotNull @Length(max = 30) String maternalSurname,
        @Past LocalDate birthdate,
        @Min(0) @Max(18) int age,
        @Null @Length(max = 255) String presumptiveDiagnosis,  // Campo correcto
        boolean status,
        @NotNull Long idPlan
) {}


