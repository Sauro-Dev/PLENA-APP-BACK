package com.plenamente.sgt.domain.dto.PatientDto;

import com.plenamente.sgt.domain.dto.TutorDto.TutorDTO;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

public record UpdatePatient(
        @NotNull Long idPatient,
        @NotBlank @Length(min = 2, max = 30) String name,
        @NotBlank @Length(min = 2, max = 30) String paternalSurname,
        @NotNull @Pattern(regexp = "\\d{8}") String dni,
        @NotNull @Length(max = 30) String maternalSurname,
        @Past LocalDate birthdate,
        @Null @Length(max = 255) String presumptiveDiagnosis,
        boolean status,
        @NotNull Long idPlan,
        List<TutorDTO> tutors
) {
}


