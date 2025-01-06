package com.plenamente.sgt;

import com.plenamente.sgt.domain.dto.SessionDto.RegisterSession;
import com.plenamente.sgt.domain.entity.*;
import com.plenamente.sgt.infra.exception.ResourceNotFoundException;
import com.plenamente.sgt.infra.repository.*;
import com.plenamente.sgt.service.impl.SessionServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "spring.profiles.active=test")
public class SessionServiceImplTest {

    @MockBean
    private SessionRepository sessionRepository;

    @MockBean
    private PatientRepository patientRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoomRepository roomRepository;

    @Autowired
    private SessionServiceImpl sessionService;

    @BeforeEach
    void setUp() {
        System.out.println("\n=== Iniciando nuevo test para session ===");
    }

    @Test
    void createSessionThrowsExceptionWhenInvalidTimeProvided() {
        // Arrange
        RegisterSession dto = new RegisterSession(
                LocalTime.of(8, 0), 1L, 1L, 1L, List.of(LocalDate.now())
        );

        // Act & Assert
        assertThatThrownBy(() -> sessionService.createSession(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Una de las fechas o horarios proporcionados es inválida para programar una sesión.");
    }

    @Test
    void createSessionThrowsExceptionWhenPatientNotFound() {
        // Arrange
        RegisterSession dto = new RegisterSession(
                LocalTime.of(10, 0), 1L, 1L, 1L, List.of(LocalDate.now())
        );

        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sessionService.createSession(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Paciente no encontrado");
    }

    @Test
    void createSessionThrowsExceptionWhenTherapistNotFound() {
        // Arrange
        RegisterSession dto = new RegisterSession(
                LocalTime.of(10, 0), 1L, 1L, 1L, List.of(LocalDate.now())
        );

        when(patientRepository.findById(1L)).thenReturn(Optional.of(new Patient()));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sessionService.createSession(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Terapeuta no encontrado");
    }

    @Test
    void createSessionThrowsExceptionWhenRoomNotFound() {
        // Arrange
        RegisterSession dto = new RegisterSession(
                LocalTime.of(10, 0), 1L, 1L, 1L, List.of(LocalDate.now())
        );

        when(patientRepository.findById(1L)).thenReturn(Optional.of(new Patient()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new Therapist()));
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sessionService.createSession(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Sala no encontrada");
    }

    @Test
    void createSessionThrowsExceptionWhenPlanIsInvalid() {
        // Arrange
        RegisterSession dto = new RegisterSession(
                LocalTime.of(10, 0), 1L, 1L, 1L, List.of(LocalDate.now())
        );

        Patient patient = new Patient();
        patient.setIdPlan(new Plan());

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new Therapist()));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(new Room()));

        // Act & Assert
        assertThatThrownBy(() -> sessionService.createSession(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El paciente debe tener un plan válido con sesiones asignadas y una duración de semanas definida.");
    }

    @Test
    void getSessionsByTherapistThrowsExceptionWhenTherapistNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sessionService.getSessionsByTherapist(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Terapeuta no encontrado");
    }

    @Test
    void getSessionsByDateThrowsExceptionWhenNoSessionsFound() {
        // Arrange
        LocalDate date = LocalDate.now();
        when(sessionRepository.findBySessionDate(date)).thenReturn(List.of());

        // Act & Assert
        assertThatThrownBy(() -> sessionService.getSessionsByDate(date))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No se encontraron sesiones para la fecha " + date);
    }

    @Test
    void getSessionsByRoomThrowsExceptionWhenRoomNotFound() {
        // Arrange
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sessionService.getSessionsByRoom(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Sala no encontrada.");
    }

    @Test
    void getSessionsByRoomThrowsExceptionWhenNoSessionsFound() {
        // Arrange
        Room room = new Room();
        room.setName("Room 1");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(sessionRepository.findByRoom_IdRoom(1L)).thenReturn(List.of());

        // Act & Assert
        assertThatThrownBy(() -> sessionService.getSessionsByRoom(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No se encontraron sesiones para la sala " + room.getName());
    }
}