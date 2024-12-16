package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.PatientDto.RegisterPatient;
import com.plenamente.sgt.domain.dto.PatientDto.UpdatePatient;
import com.plenamente.sgt.domain.dto.PatientDto.ListPatient;
import com.plenamente.sgt.domain.entity.Patient;
import com.plenamente.sgt.domain.entity.Plan;
import com.plenamente.sgt.domain.entity.Tutor;
import com.plenamente.sgt.infra.repository.PatientRepository;
import com.plenamente.sgt.infra.exception.ResourceNotFoundException;
import com.plenamente.sgt.infra.repository.PlanRepository;
import com.plenamente.sgt.infra.repository.TutorRepository;
import com.plenamente.sgt.service.PatientService;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PlanRepository planRepository;
    private final TutorRepository tutorRepository;

    @Override
    public Patient createPatient(RegisterPatient registerPatient) {
        // Validación del apoderado
        if (registerPatient.tutors().isEmpty() || registerPatient.tutors().get(0) == null) {
            throw new IllegalArgumentException("El campo apoderado 1 es obligatorio.");
        }

        // Validación de los días según el plan
        //Plan plan = registerPatient.idPlan(); // Asumiendo que Plan tiene un metodo para obtener días
        // Aqui puedes poner la validacion que gustes cuando hagas el dto de plan y todo eso ps xd

        Plan plan = planRepository.findById(registerPatient.idPlan())
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado."));

        // Crear paciente
        Patient patient = new Patient();


        patient.setName(registerPatient.name());
        patient.setPaternalSurname(registerPatient.paternalSurname());
        patient.setMaternalSurname(registerPatient.maternalSurname());
        patient.setBirthdate(registerPatient.birthdate());
        patient.setAge(registerPatient.age());
        patient.setAllergies(registerPatient.allergies());
        patient.setIdPlan(plan);
        patient.setTutors(registerPatient.tutors());

        if(registerPatient.tutors().size() == 1){
            Tutor tutor = registerPatient.tutors().get(0);
            tutorRepository.save(tutor);
        }
        if(registerPatient.tutors().size() == 2) {
            Tutor tutor1 = registerPatient.tutors().get(0);
            Tutor tutor2 = registerPatient.tutors().get(1);
            tutorRepository.save(tutor1);
            tutorRepository.save(tutor2);
        }


        return patientRepository.save(patient);
    }

    @Override
    public List<ListPatient> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();

        // Obtener cantidad de pacientes
        int patientCount = patients.size();
        System.out.println("Total de pacientes: " + patientCount);

        // Mapear a ListPatient
        return patients.stream().map(this::mapToListPatient).collect(Collectors.toList());
    }

    @Override
    public ListPatient getPatientById(Long id) {
        // Obtener paciente por ID
        Patient patient = patientRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Paciente no encontrado.")
        );
        return mapToListPatient(patient);
    }

    @Override
    public Patient updatePatient(Long id, UpdatePatient updatePatient) {
        // Obtener paciente existente
        Patient existingPatient = patientRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Paciente no encontrado.")
        );

        // Actualizar información del paciente
        existingPatient.setName(updatePatient.name());
        existingPatient.setPaternalSurname(updatePatient.paternalSurname());
        existingPatient.setMaternalSurname(updatePatient.maternalSurname());
        existingPatient.setBirthdate(updatePatient.birthdate());
        existingPatient.setAge(updatePatient.age());
        existingPatient.setAllergies(updatePatient.allergies());

        return patientRepository.save(existingPatient);
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
                patient.getAge(),
                patient.getIdPlan().getIdPlan(),
                patient.getTutors().stream().map(Tutor::getFullName).collect(Collectors.toList()),
                patient.isStatus()
        );
    }
}
