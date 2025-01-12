package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.PatientDto.RegisterPatient;
import com.plenamente.sgt.domain.dto.PatientDto.RenewPlanDto;
import com.plenamente.sgt.domain.dto.PatientDto.UpdatePatient;
import com.plenamente.sgt.domain.dto.PatientDto.ListPatient;
import com.plenamente.sgt.domain.dto.SessionDto.RegisterSession;
import com.plenamente.sgt.domain.dto.TutorDto.TutorDTO;
import com.plenamente.sgt.domain.entity.*;
import com.plenamente.sgt.infra.repository.PatientRepository;
import com.plenamente.sgt.infra.exception.ResourceNotFoundException;
import com.plenamente.sgt.infra.repository.PlanRepository;
import com.plenamente.sgt.infra.repository.SessionRepository;
import com.plenamente.sgt.service.PatientService;
import com.plenamente.sgt.service.SessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PlanRepository planRepository;
    private final SessionService sessionService;
    private final SessionRepository sessionRepository;

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

        LocalDate firstSessionDate = registerPatient.firstWeekDates()
                .stream()
                .min(LocalDate::compareTo)
                .orElseThrow(() -> new IllegalStateException("No se encontraron fechas para las sesiones iniciales."));

        setInitialPlanStatus(patient, firstSessionDate);

        Patient savedPatient = patientRepository.save(patient);

        RegisterSession sessionData = new RegisterSession(
                registerPatient.startTime(),
                savedPatient.getIdPatient(),
                registerPatient.therapistId(),
                registerPatient.roomId(),
                registerPatient.firstWeekDates(),
                0 // Inicializar renewPlan en 0 para la primera creación
        );

        sessionService.createSession(sessionData);

        return savedPatient;
    }

    @Override
    public List<ListPatient> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();

        patients.forEach(patient -> {
            try {
                calculatePlanStatus(patient);
            } catch (Exception e) {
                throw new IllegalStateException("Error al calcular el estado del plan para el paciente: " +
                        patient.getName(), e);
            }
        });

        patients = patients.stream().distinct().toList();
        patientRepository.saveAll(patients);

        return patients.stream()
                .map(this::mapToListPatient)
                .collect(Collectors.toList());
    }

    @Override
    public ListPatient getPatientById(Long id) {
        Patient patient = patientRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Paciente no encontrado.")
        );

        calculatePlanStatus(patient);
        patientRepository.save(patient);

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

        if (updatePatient.tutors() != null) {
            List<Tutor> existingTutors = existingPatient.getTutors();
            if (existingTutors.size() != updatePatient.tutors().size()) {
                throw new IllegalArgumentException("El número de tutores proporcionados no coincide con los tutores registrados.");
            }

            for (int i = 0; i < updatePatient.tutors().size(); i++) {
                Tutor existingTutor = existingTutors.get(i);
                TutorDTO tutorDto = updatePatient.tutors().get(i);
                validateTutorData(tutorDto);

                existingTutor.setFullName(tutorDto.fullName());
                existingTutor.setDni(tutorDto.dni());
                existingTutor.setPhone(tutorDto.phone());
                existingTutor.setPatient(existingPatient);
            }
        }

        return patientRepository.save(existingPatient);
    }

    @Transactional
    @Override
    public Patient renewPlan(RenewPlanDto renewPlanDto) {
        Patient patient = patientRepository.findById(renewPlanDto.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado."));

        if (patient.isStatus()) {
            throw new IllegalStateException("El paciente ya está activo. No es posible renovar el plan.");
        }

        if (!PlanStatus.COMPLETADO.equals(patient.getPlanStatus())) {
            throw new IllegalStateException("El plan actual del paciente no está completado. No se puede renovar.");
        }

        Plan newPlan = planRepository.findById(renewPlanDto.newPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado."));

        patient.setIdPlan(newPlan);
        patient.setStatus(true);

        int maxRenewPlan = sessionRepository.findMaxRenewPlanByPatientId(patient.getIdPatient()).orElse(0);

        RegisterSession sessionData = new RegisterSession(
                renewPlanDto.startTime(),
                renewPlanDto.patientId(),
                renewPlanDto.therapistId(),
                renewPlanDto.roomId(),
                renewPlanDto.firstWeekDates(),
                maxRenewPlan + 1 // Incrementar renewPlan
        );
        sessionService.createSession(sessionData);

        calculatePlanStatus(patient);

        return patientRepository.save(patient);
    }

    private void calculatePlanStatus(Patient patient) {
        Plan plan = patient.getIdPlan();
        List<Session> sessions = sessionRepository.findByPatient_IdPatient(patient.getIdPatient());

        if (sessions.isEmpty()) {
            throw new IllegalStateException("No hay sesiones asociadas al paciente para calcular el estado del plan.");
        }

        int maxRenewPlan = sessions.stream()
                .mapToInt(Session::getRenewPlan)
                .max()
                .orElse(0);

        List<Session> newPlanSessions = sessions.stream()
                .filter(session -> session.getPlan().getIdPlan().equals(plan.getIdPlan()) && session.getRenewPlan() == maxRenewPlan)
                .toList();

        if (newPlanSessions.isEmpty()) {
            throw new IllegalStateException("No hay sesiones asociadas al nuevo plan del paciente.");
        }

        LocalDate startDate = newPlanSessions.stream()
                .map(Session::getSessionDate)
                .min(LocalDate::compareTo)
                .orElseThrow(() -> new IllegalStateException("No se pudo calcular la fecha de inicio del nuevo plan."));

        LocalDate endDate = newPlanSessions.stream()
                .map(Session::getSessionDate)
                .max(LocalDate::compareTo)
                .orElseThrow(() -> new IllegalStateException("No se pudo calcular la fecha de finalización del nuevo plan."));

        LocalDate today = LocalDate.now();
        System.out.println("startDate: " + startDate);
        System.out.println("endDate: " + endDate);
        System.out.println("today: " + today);

        if (today.isBefore(startDate)) {
            patient.setPlanStatus(PlanStatus.EN_ESPERA);
        } else if (today.isAfter(endDate)) {
            updatePatientStatusToInactive(patient);
            patient.setPlanStatus(PlanStatus.COMPLETADO);
        } else {
            long completedSessions = newPlanSessions.stream().filter(this::isSessionValid).count();
            int totalSessions = plan.getWeeks() * plan.getNumOfSessions();

            if (completedSessions >= totalSessions) {
                updatePatientStatusToInactive(patient);
                patient.setPlanStatus(PlanStatus.COMPLETADO);
            } else {
                patient.setPlanStatus(PlanStatus.EN_CURSO);
            }
        }

        patientRepository.save(patient);
    }

    private void setInitialPlanStatus(Patient patient, LocalDate firstSessionDate) {
        LocalDate today = LocalDate.now();
        if (today.isBefore(firstSessionDate)) {
            patient.setPlanStatus(PlanStatus.EN_ESPERA);
        } else {
            patient.setPlanStatus(PlanStatus.EN_CURSO);
        }
    }

    private void updatePatientStatusToInactive(Patient patient) {
        if (patient.isStatus()) {
            patient.setStatus(false);
            patientRepository.save(patient);
        }
    }

    private LocalDate calculatePlanEndDate(LocalDate startDate, int weeks) {
        return startDate.plusWeeks(weeks - 1);
    }

    private boolean isSessionValid(Session session) {
        boolean therapistPresent = session.isTherapistPresent();
        boolean patientCondition = session.isPatientPresent() || session.getReason() == null;
        boolean sessionFinished = session.getEndTime().isBefore(LocalTime.now())
                && session.getSessionDate().isBefore(LocalDate.now());

        return therapistPresent && patientCondition && sessionFinished;
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

    private void validateTutorData(TutorDTO tutorDto) {
        if (tutorDto.fullName() == null || tutorDto.fullName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre completo del tutor no puede estar vacío.");
        }

        if (tutorDto.dni() == null || !tutorDto.dni().matches("\\d+")) {
            throw new IllegalArgumentException("El DNI del tutor debe ser numérico y no nulo.");
        }

        if (tutorDto.phone() == null || !tutorDto.phone().matches("\\d+")) {
            throw new IllegalArgumentException("El teléfono del tutor debe ser numérico y no nulo.");
        }
    }

    private ListPatient mapToListPatient(Patient patient) {
        if (PlanStatus.COMPLETADO.equals(patient.getPlanStatus()) && patient.isStatus()) {
            updatePatientStatusToInactive(patient);
        }

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
                patient.isStatus(),
                patient.getPlanStatus().name()
        );
    }
}