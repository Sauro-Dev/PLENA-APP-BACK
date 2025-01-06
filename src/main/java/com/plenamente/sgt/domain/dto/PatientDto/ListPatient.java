package com.plenamente.sgt.domain.dto.PatientDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plenamente.sgt.domain.dto.TutorDto.TutorDTO;

import java.time.LocalDate;
import java.util.List;

public record ListPatient(
        Long idPatient,
        String name,
        String paternalSurname,
        String maternalSurname,
        String dni,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate birthdate,
        int age,
        Long planId,
        List<TutorDTO> tutors,
        String presumptiveDiagnosis,
        boolean status
) {}
