package com.plenamente.sgt;

import com.plenamente.sgt.domain.dto.PatientDto.ListPatient;
import com.plenamente.sgt.domain.dto.PatientDto.RegisterPatient;
import com.plenamente.sgt.domain.dto.PatientDto.UpdatePatient;
import com.plenamente.sgt.domain.dto.TutorDto.TutorDTO;
import com.plenamente.sgt.domain.entity.Patient;
import com.plenamente.sgt.domain.entity.Plan;
import com.plenamente.sgt.domain.entity.Tutor;
import com.plenamente.sgt.infra.exception.ResourceNotFoundException;
import com.plenamente.sgt.infra.repository.PatientRepository;
import com.plenamente.sgt.infra.repository.PlanRepository;
import com.plenamente.sgt.service.SessionService;
import com.plenamente.sgt.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "spring.profiles.active=test")
public class PatientServiceImplTest {

    @MockBean
    private PatientRepository patientRepository;

    @MockBean
    private PlanRepository planRepository;

    @MockBean
    private SessionService sessionService;

    @Autowired
    private PatientServiceImpl patientService;

    @BeforeEach
    void setUp() {
        System.out.println("\n=== Iniciando nuevo test para patient ===");
    }

    @Test
    void createPatientWithValidDataCreatesPatient() {
        // Arrange
        Tutor tutor = new Tutor();
        tutor.setFullName("Jane Doe");
        tutor.setDni("87654321");
        tutor.setPhone("123456789");

        RegisterPatient registerPatient = new RegisterPatient(
                "John",
                "Doe",
                "12345678",
                "Smith",
                LocalDate.of(2000, 1, 1),
                "Diagnosis",
                true,
                1L,
                List.of(tutor),
                1L,
                1L,
                LocalTime.now(),
                List.of(LocalDate.now())
        );
        Plan plan = new Plan();
        plan.setIdPlan(1L);
        Patient patient = new Patient();
        patient.setIdPatient(1L);

        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // Act
        Patient result = patientService.createPatient(registerPatient);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getIdPatient()).isEqualTo(1L);
    }

    @Test
    void createPatientWithExistingDniThrowsException() {
        // Arrange
        Tutor tutor = new Tutor();
        tutor.setFullName("Jane Doe");
        tutor.setDni("87654321");
        tutor.setPhone("123456789");

        RegisterPatient registerPatient = new RegisterPatient(
                "John",
                "Doe",
                "12345678",
                "Smith",
                LocalDate.of(2000, 1, 1),
                "Diagnosis",
                true,
                1L,
                List.of(tutor),
                1L,
                1L,
                LocalTime.now(),
                List.of(LocalDate.now())
        );

        when(patientRepository.existsByDni("12345678")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> patientService.createPatient(registerPatient))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El DNI ya está registrado en el sistema.");
    }

    @Test
    void createPatientWithInvalidPlanThrowsException() {
        // Arrange
        Tutor tutor = new Tutor();
        tutor.setFullName("Jane Doe");
        tutor.setDni("87654321");
        tutor.setPhone("123456789");

        RegisterPatient registerPatient = new RegisterPatient(
                "John",
                "Doe",
                "12345678",
                "Smith",
                LocalDate.of(2000, 1, 1),
                "Diagnosis",
                true,
                1L,
                List.of(tutor),
                1L,
                1L,
                LocalTime.now(),
                List.of(LocalDate.now())
        );

        when(planRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> patientService.createPatient(registerPatient))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Plan no encontrado.");
    }

    @Test
    void getAllPatientsReturnsListOfPatients() {
        // Arrange
        Plan plan = new Plan();
        plan.setIdPlan(1L);

        Tutor tutor = new Tutor();
        tutor.setFullName("Jane Doe");
        tutor.setDni("87654321");
        tutor.setPhone("123456789");

        Patient patient = new Patient();
        patient.setIdPlan(plan);
        patient.setTutors(List.of(tutor));

        List<Patient> patients = List.of(patient);
        when(patientRepository.findAll()).thenReturn(patients);

        // Act
        List<ListPatient> result = patientService.getAllPatients();

        // Assert
        assertThat(result).hasSize(1);
    }

    @Test
    void getPatientByIdWithValidIdReturnsPatient() {
        // Arrange
        Plan plan = new Plan();
        plan.setIdPlan(1L);

        Tutor tutor = new Tutor();
        tutor.setFullName("Jane Doe");
        tutor.setDni("87654321");
        tutor.setPhone("123456789");

        Patient patient = new Patient();
        patient.setIdPatient(1L);
        patient.setIdPlan(plan);
        patient.setTutors(List.of(tutor));

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // Act
        ListPatient result = patientService.getPatientById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.idPatient()).isEqualTo(1L);
    }

    @Test
    void getPatientByIdWithInvalidIdThrowsException() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> patientService.getPatientById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Paciente no encontrado.");
    }

    @Test
    void updatePatientWithValidDataUpdatesPatient() {
        // Arrange
        Long patientId = 1L;
        TutorDTO tutorDTO = new TutorDTO("Jane Doe", "87654321", "123456789");

        UpdatePatient updatePatient = new UpdatePatient(
                1L,
                "John",
                "Doe",
                "12345678",
                "Smith",
                LocalDate.of(2000, 1, 1),
                "Diagnosis",
                true,
                1L,
                List.of(tutorDTO)
        );
        Patient existingPatient = new Patient();
        existingPatient.setIdPatient(patientId);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(existingPatient)).thenReturn(existingPatient);

        // Act
        Patient result = patientService.updatePatient(patientId, updatePatient);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John");
    }

    @Test
    void updatePatientWithInvalidIdThrowsException() {
        // Arrange
        Long patientId = 1L;
        TutorDTO tutorDTO = new TutorDTO("Jane Doe", "87654321", "123456789");

        UpdatePatient updatePatient = new UpdatePatient(
                1L,
                "John",
                "Doe",
                "12345678",
                "Smith",
                LocalDate.of(2000, 1, 1),
                "Diagnosis",
                true,
                1L,
                List.of(tutorDTO)
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> patientService.updatePatient(patientId, updatePatient))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Paciente no encontrado.");
    }

    @Test
    void isDNITakenReturnsTrueWhenDniExists() {
        // Arrange
        String dni = "12345678";
        when(patientRepository.existsByDni(dni)).thenReturn(true);

        // Act
        boolean result = patientService.isDNITaken(dni);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void isDNITakenReturnsFalseWhenDniDoesNotExist() {
        // Arrange
        String dni = "12345678";
        when(patientRepository.existsByDni(dni)).thenReturn(false);

        // Act
        boolean result = patientService.isDNITaken(dni);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void filterPatientsByNameReturnsFilteredPatients() {
        // Arrange
        String name = "John";
        Plan plan = new Plan();
        plan.setIdPlan(1L);

        Tutor tutor = new Tutor();
        tutor.setFullName("Jane Doe");
        tutor.setDni("87654321");
        tutor.setPhone("123456789");

        Patient patient = new Patient();
        patient.setIdPlan(plan);
        patient.setTutors(List.of(tutor));

        List<Patient> patients = List.of(patient);
        when(patientRepository.findByNameContainingIgnoreCase(name)).thenReturn(patients);

        // Act
        List<ListPatient> result = patientService.filterPatientsByName(name);

        // Assert
        assertThat(result).hasSize(1);
    }

    @Test
    void filterPatientsByPlanReturnsFilteredPatients() {
        // Arrange
        Long planId = 1L;
        Plan plan = new Plan();
        plan.setIdPlan(planId);

        Tutor tutor = new Tutor();
        tutor.setFullName("Jane Doe");
        tutor.setDni("87654321");
        tutor.setPhone("123456789");

        Patient patient = new Patient();
        patient.setIdPlan(plan);
        patient.setTutors(List.of(tutor));

        List<Patient> patients = List.of(patient);
        when(patientRepository.findByIdPlanIdPlan(planId)).thenReturn(patients);

        // Act
        List<ListPatient> result = patientService.filterPatientsByPlan(planId);

        // Assert
        assertThat(result).hasSize(1);
    }

    @Test
    void orderPatientsByNameAscReturnsOrderedPatients() {
        // Arrange
        Plan plan = new Plan();
        plan.setIdPlan(1L);

        Tutor tutor = new Tutor();
        tutor.setFullName("Jane Doe");
        tutor.setDni("87654321");
        tutor.setPhone("123456789");

        Patient patient = new Patient();
        patient.setIdPlan(plan);
        patient.setTutors(List.of(tutor));

        List<Patient> patients = List.of(patient);
        when(patientRepository.findAllByOrderByNameAsc()).thenReturn(patients);

        // Act
        List<ListPatient> result = patientService.orderPatientsByName("asc");

        // Assert
        assertThat(result).hasSize(1);
    }

    @Test
    void orderPatientsByNameDescReturnsOrderedPatients() {
        // Arrange
        Plan plan = new Plan();
        plan.setIdPlan(1L);

        Tutor tutor = new Tutor();
        tutor.setFullName("Jane Doe");
        tutor.setDni("87654321");
        tutor.setPhone("123456789");

        Patient patient = new Patient();
        patient.setIdPlan(plan);
        patient.setTutors(List.of(tutor));

        List<Patient> patients = List.of(patient);
        when(patientRepository.findAllByOrderByNameDesc()).thenReturn(patients);

        // Act
        List<ListPatient> result = patientService.orderPatientsByName("desc");

        // Assert
        assertThat(result).hasSize(1);
    }
}