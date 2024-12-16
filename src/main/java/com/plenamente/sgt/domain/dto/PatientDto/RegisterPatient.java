package com.plenamente.sgt.domain.dto.PatientDto;

import com.plenamente.sgt.domain.entity.Plan;
import com.plenamente.sgt.domain.entity.Tutor;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public record RegisterPatient(
        @NotBlank @Length(min = 2, max = 30) String name,
        @NotBlank @Length(min = 2, max = 30) String paternalSurname,
        @Length(max = 30) String maternalSurname,
        @Past LocalDate birthdate,
        @Min(0) @Max(18) int age,
        @Length(max = 255) String allergies,
        boolean status,
        @NotNull Long idPlan,
        @NotEmpty @Size(min = 1, max = 2) List<Tutor> tutors
) {
    public RegisterPatient(
            @NotBlank @Length(min = 2, max = 30) String name,
            @NotBlank @Length(min = 2, max = 30) String paternalSurname,
            @Length(max = 30) String maternalSurname,
            @Past LocalDate birthdate,
            @Min(0) @Max(18) int age,
            @Length(max = 255) String allergies,
            @NotNull Long idPlan,
            @NotEmpty @Size(min = 1, max = 2) List<Tutor> tutors
    )
    {
        this(name, paternalSurname, maternalSurname, birthdate, age, allergies, true, idPlan, tutors);
    }
}
