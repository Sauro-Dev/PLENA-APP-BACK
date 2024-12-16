package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.SessionDto.ListSession;
import com.plenamente.sgt.domain.dto.SessionDto.MarkPresenceSession;
import com.plenamente.sgt.domain.dto.SessionDto.RegisterSession;
import com.plenamente.sgt.domain.dto.SessionDto.UpdateSession;
import com.plenamente.sgt.domain.dto.UserDto.ListTherapist;
import com.plenamente.sgt.domain.dto.UserDto.ListUser;
import com.plenamente.sgt.domain.entity.*;
import com.plenamente.sgt.infra.exception.ResourceNotFoundException;
import com.plenamente.sgt.infra.repository.*;
import com.plenamente.sgt.service.SessionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PlanRepository planRepository;

    @Override
    public Session createSession(RegisterSession dto) {
        Patient patient = patientRepository.findById(dto.patientId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
        User user = userRepository.findById(dto.therapistId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        if (!(user instanceof Therapist therapist)) {
            throw new IllegalArgumentException("El usuario no es un terapeuta");
        }
        Room room = roomRepository.findById(dto.roomId())
                .orElseThrow(() -> new EntityNotFoundException("Sala no encontrada"));
        Plan plan = planRepository.findById(dto.planId())
                .orElseThrow(() -> new EntityNotFoundException("Plan no encontrado"));

        Session session = new Session();
        session.setSessionDate(dto.sessionDate());
        session.setStartTime(dto.startTime());
        session.setEndTime(dto.endTime());
        session.setPatient(patient);
        session.setTherapist(therapist);
        session.setRoom(room);
        session.setPlan(plan);
        return sessionRepository.save(session);
    }

    @Override
    public List<ListSession> getSessionsByTherapist(Long therapistId) {
        // Verificar que el terapeuta existe
        userRepository.findById(therapistId)
                .filter(user -> user instanceof Therapist)
                .orElseThrow(() -> new EntityNotFoundException("Terapeuta no encontrado"));

        // Consultar las sesiones asignadas al terapeuta
        return sessionRepository.findByTherapist_IdUser(therapistId)
                .stream()
                .map(session -> new ListSession(
                        session.getIdSession(),
                        session.getSessionDate(),
                        session.getStartTime(),
                        session.getEndTime(),
                        session.getPatient().getName(),
                        session.getTherapist().getName(),
                        session.getRoom().getName(),
                        session.isRescheduled(),
                        session.isTherapistPresent(),
                        session.isPatientPresent()
                ))
                .collect(Collectors.toList());
    }


    @Override
    public List<ListSession> getSessionsByDate(LocalDate date) {
        List<Session> sessions = sessionRepository.findBySessionDate(date);

        if (sessions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron sesiones para la fecha " + date);
        }

        return sessions.stream()
                .map(session -> new ListSession(
                        session.getIdSession(),
                        session.getSessionDate(),
                        session.getStartTime(),
                        session.getEndTime(),
                        session.getPatient().getName(),
                        session.getTherapist().getName(),
                        session.getRoom().getName(),
                        session.isRescheduled(),
                        session.isTherapistPresent(),
                        session.isPatientPresent()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Session updateSession(UpdateSession dto) {
        // Usamos el idSession que se pasa en la URL (ya que se ha asignado al DTO en el controlador)
        Session session = sessionRepository.findByIdSession(dto.idSession())
                .orElseThrow(() -> new EntityNotFoundException("Sesión no encontrada"));

        session.setSessionDate(dto.sessionDate());
        session.setStartTime(dto.startTime());
        session.setEndTime(dto.endTime());
        session.setReason(dto.reason());
        session.setRescheduled(true);

        return sessionRepository.save(session);
    }

    @Override
    public Session markPresence(MarkPresenceSession dto) {
        // Obtener la sesión por el ID
        Session session = sessionRepository.findByIdSession(dto.sessionId())
                .orElseThrow(() -> new EntityNotFoundException("Sesión no encontrada"));

        // Marcar la presencia del terapeuta y paciente
        session.setTherapistPresent(dto.therapistPresent());
        session.setPatientPresent(dto.patientPresent());

        // Guardar la sesión actualizada
        return sessionRepository.save(session);
    }

    @Override
    public void assignSessionsFromSession(Long sessionId) {
        Session initialSession = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Sesión inicial no encontrada"));

        LocalDate startDate = initialSession.getSessionDate();
        Patient patient = initialSession.getPatient();
        Plan plan = initialSession.getPlan();

        if (plan == null) {
            throw new IllegalArgumentException("El paciente no tiene un plan válido asociado a la sesión");
        }

        // Crea 1 sesión por semana durante 4 semanas (un mes)
        List<LocalDate> sessionDates = calculateWeeklySessionDates(startDate);

        for (LocalDate sessionDate : sessionDates) {
            // Evitar duplicar la sesión inicial
            if (sessionDate.isEqual(startDate)) {
                continue;
            }

            Session newSession = new Session();
            newSession.setSessionDate(sessionDate);
            newSession.setPatient(patient);
            newSession.setPlan(plan);
            newSession.setTherapist(initialSession.getTherapist());
            newSession.setRoom(initialSession.getRoom());
            newSession.setTherapistPresent(false);
            newSession.setPatientPresent(false);
            newSession.setEndTime(initialSession.getEndTime());
            newSession.setStartTime(initialSession.getStartTime());

            sessionRepository.save(newSession);
        }
    }

    private List<LocalDate> calculateWeeklySessionDates(LocalDate startDate) {
        List<LocalDate> dates = new ArrayList<>();

        // Generar fechas: una por semana durante 4 semanas
        for (int i = 0; i < 4; i++) {
            dates.add(startDate.plusWeeks(i));
        }

        return dates;
    }


    @Override
    public List<ListTherapist> getAvailableTherapist(LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<User> therapists = userRepository.findByRol(Rol.THERAPIST);

        // Filtrar los terapeutas disponibles, y mapearlos a la clase ListTherapist
        return therapists.stream()
                .filter(therapist -> isTherapistAvailable(therapist.getIdUser(), date, startTime, endTime))
                .map(therapist -> new ListTherapist(
                        therapist.getIdUser(),
                        therapist.getName()
                )).collect(Collectors.toList());
    }

    public boolean isTherapistAvailable(Long therapistId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return !sessionRepository.existsByTherapist_IdUserAndSessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                therapistId, date, endTime, startTime);
    }

}
