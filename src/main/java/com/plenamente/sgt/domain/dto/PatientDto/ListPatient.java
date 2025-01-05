package com.plenamente.sgt.domain.dto.PatientDto;

import com.plenamente.sgt.domain.dto.TutorDto.TutorDTO;

import java.time.LocalDate;
import java.util.List;

public record ListPatient(
        Long idPatient,
        String name,
        String paternalSurname,
        String maternalSurname,
        String dni,
        LocalDate birthdate,
        int age,
        Long planId,
        List<TutorDTO> tutors,
        String presumptiveDiagnosis,
        boolean status
) {}
