package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.PatientDto.RegisterPatient;
import com.plenamente.sgt.domain.dto.PatientDto.RenewPlanDto;
import com.plenamente.sgt.domain.dto.PatientDto.UpdatePatient;
import com.plenamente.sgt.domain.dto.PatientDto.ListPatient;
import com.plenamente.sgt.domain.entity.Patient;
import jakarta.transaction.Transactional;

import java.util.List;

public interface PatientService {

    Patient createPatient(RegisterPatient registerPatient);
    List<ListPatient> getAllPatients();
    ListPatient getPatientById(Long id);
    Patient updatePatient(Long id, UpdatePatient updatePatient);
    List<ListPatient> filterPatientsByName(String name);
    List<ListPatient> filterPatientsByPlan(Long planId);
    List<ListPatient> orderPatientsByName(String order);

    @Transactional
    Patient renewPlan(RenewPlanDto renewPlanDto);
    boolean isDNITaken(String dni);
}
