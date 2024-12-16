package com.plenamente.sgt.domain.dto.PatientDto;

import java.util.List;

public record ListPatient(
        Long idPatient,
        String name,
        String paternalSurname,
        String maternalSurname,
        int age,
        Long planId,
        List<String> tutorNames,
        boolean status
) {}
