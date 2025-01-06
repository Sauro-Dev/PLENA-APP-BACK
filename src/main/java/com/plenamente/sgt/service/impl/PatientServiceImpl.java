package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.PatientDto.RegisterPatient;
import com.plenamente.sgt.domain.dto.PatientDto.UpdatePatient;
import com.plenamente.sgt.domain.dto.PatientDto.ListPatient;
import com.plenamente.sgt.domain.dto.SessionDto.RegisterSession;
import com.plenamente.sgt.domain.dto.TutorDto.TutorDTO;
import com.plenamente.sgt.domain.entity.Patient;
import com.plenamente.sgt.domain.entity.Plan;
import com.plenamente.sgt.domain.entity.Tutor;
import com.plenamente.sgt.infra.repository.PatientRepository;
import com.plenamente.sgt.infra.exception.ResourceNotFoundException;
import com.plenamente.sgt.infra.repository.PlanRepository;
import com.plenamente.sgt.service.PatientService;
import com.plenamente.sgt.service.SessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PlanRepository planRepository;
    private final SessionService sessionService;

    @Transactional
    @Override
    public Patient createPatient(RegisterPatient registerPatient) {
        if (patientRepository.existsByDni(registerPatient.dni())) {
            throw new IllegalArgumentException("El DNI ya está registrado en el sistema.");
        }

        Plan plan = planRepository.findById(registerPatient.idPlan())
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado."));

        Patient patient = new Patient();
        patient.setName(registerPatient.name());
        patient.setPaternalSurname(registerPatient.paternalSurname());
        patient.setMaternalSurname(registerPatient.maternalSurname());
        patient.setDni(registerPatient.dni());
        patient.setBirthdate(registerPatient.birthdate());
        patient.setPresumptiveDiagnosis(registerPatient.presumptiveDiagnosis());
        patient.setIdPlan(plan);

        int calculatedAge = Period.between(registerPatient.birthdate(), LocalDate.now()).getYears();
        patient.setAge(calculatedAge);

        List<Tutor> tutors = registerPatient.tutor().stream()
                .peek(tutor -> tutor.setPatient(patient))
                .collect(Collectors.toList());

        patient.setTutors(tutors);

        Patient savedPatient = patientRepository.save(patient);

        RegisterSession sessionData = new RegisterSession(
                registerPatient.startTime(),
                savedPatient.getIdPatient(),
                registerPatient.therapistId(),
                registerPatient.roomId(),
                registerPatient.firstWeekDates()
        );

        sessionService.createSession(sessionData);

        return savedPatient;
    }

    @Override
    public List<ListPatient> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        int patientCount = patients.size();
        System.out.println("Total de pacientes: " + patientCount);

        return patients.stream().map(this::mapToListPatient).collect(Collectors.toList());
    }

    @Override
    public ListPatient getPatientById(Long id) {
        Patient patient = patientRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Paciente no encontrado.")
        );
        return mapToListPatient(patient);
    }

    @Override
    public Patient updatePatient(Long id, UpdatePatient updatePatient) {
        Patient existingPatient = patientRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Paciente no encontrado.")
        );

        existingPatient.setName(updatePatient.name());
        existingPatient.setPaternalSurname(updatePatient.paternalSurname());
        existingPatient.setMaternalSurname(updatePatient.maternalSurname());
        existingPatient.setDni(updatePatient.dni());
        existingPatient.setBirthdate(updatePatient.birthdate());
        existingPatient.setPresumptiveDiagnosis(updatePatient.presumptiveDiagnosis());
        existingPatient.setStatus(updatePatient.status());

        int calculatedAge = Period.between(updatePatient.birthdate(), LocalDate.now()).getYears();
        existingPatient.setAge(calculatedAge);

        return patientRepository.save(existingPatient);
    }

    @Override
    public boolean isDNITaken(String dni) {
        return patientRepository.existsByDni(dni);
    }

    @Override
    public List<ListPatient> filterPatientsByName(String name) {
        return patientRepository.findByNameContainingIgnoreCase(name)
                .stream().map(this::mapToListPatient).collect(Collectors.toList());
    }

    @Override
    public List<ListPatient> filterPatientsByPlan(Long planId) {
        return patientRepository.findByIdPlanIdPlan(planId)
                .stream().map(this::mapToListPatient).collect(Collectors.toList());
    }

    @Override
    public List<ListPatient> orderPatientsByName(String order) {
        if ("asc".equalsIgnoreCase(order)) {
            return patientRepository.findAllByOrderByNameAsc()
                    .stream().map(this::mapToListPatient).collect(Collectors.toList());
        } else {
            return patientRepository.findAllByOrderByNameDesc()
                    .stream().map(this::mapToListPatient).collect(Collectors.toList());
        }
    }

    private ListPatient mapToListPatient(Patient patient) {
        return new ListPatient(
                patient.getIdPatient(),
                patient.getName(),
                patient.getPaternalSurname(),
                patient.getMaternalSurname(),
                patient.getDni(),
                patient.getBirthdate(),
                patient.getAge(),
                patient.getIdPlan().getIdPlan(),
                patient.getTutors().stream()
                        .map(tutor -> new TutorDTO(tutor.getFullName(), tutor.getDni(), tutor.getPhone()))
                        .collect(Collectors.toList()),
                patient.getPresumptiveDiagnosis(),
                patient.isStatus()
        );
    }
}
