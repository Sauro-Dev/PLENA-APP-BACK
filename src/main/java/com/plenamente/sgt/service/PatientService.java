package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.PatientDto.RegisterPatient;
import com.plenamente.sgt.domain.dto.PatientDto.UpdatePatient;
import com.plenamente.sgt.domain.dto.PatientDto.ListPatient;
import com.plenamente.sgt.domain.entity.Patient;

import java.util.List;

public interface PatientService {

    // Metodo para registrar un paciente
    Patient createPatient(RegisterPatient registerPatient);

    // Metodo para obtener todos los pacientes
    List<ListPatient> getAllPatients();

    // Metodo para obtener un paciente por ID
    ListPatient getPatientById(Long id);

    // Metodo para actualizar un paciente
    Patient updatePatient(Long id, UpdatePatient updatePatient);

    // Otros metodos de filtrado y ordenación
    List<ListPatient> filterPatientsByName(String name);
    List<ListPatient> filterPatientsByPlan(Long planId);
    List<ListPatient> orderPatientsByName(String order);
}
