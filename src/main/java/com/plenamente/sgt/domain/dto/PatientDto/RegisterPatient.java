package com.plenamente.sgt.domain.dto.PatientDto;

import com.plenamente.sgt.domain.entity.Tutor;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

public record RegisterPatient(
        @NotBlank @Length(min = 2, max = 30) String name,
        @NotBlank @Length(min = 2, max = 30) String paternalSurname,
        @NotNull @Pattern(regexp = "\\d{8}") String dni,
        @NotNull @Length(max = 30) String maternalSurname,
        @Past LocalDate birthdate,
        @Min(0) @Max(18) int age,
        @Null @Length(max = 255) String allergies,
        boolean status,
        @NotNull Long idPlan,
        @NotEmpty @Size(min = 1, max = 2) List<Tutor> tutors
) {
}
