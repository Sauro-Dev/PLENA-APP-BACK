package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.SessionDto.ListSession;
import com.plenamente.sgt.domain.dto.SessionDto.MarkPresenceSession;
import com.plenamente.sgt.domain.dto.SessionDto.RegisterSession;
import com.plenamente.sgt.domain.dto.SessionDto.UpdateSession;
import com.plenamente.sgt.domain.dto.UserDto.ListTherapist;
import com.plenamente.sgt.domain.entity.*;
import com.plenamente.sgt.infra.exception.ResourceNotFoundException;
import com.plenamente.sgt.infra.repository.*;
import com.plenamente.sgt.service.SessionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
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


    @Override
    public Session createSession(RegisterSession dto) {
        for (LocalDate date : dto.firstWeekDates()) {
            if (isInvalidTime(dto.startTime()) || isNotWorkingDay(date)) {
                throw new IllegalArgumentException("Una de las fechas o horarios proporcionados es inválida para programar una sesión.");
            }
        }
        Patient patient = patientRepository.findById(dto.patientId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
        User user = userRepository.findById(dto.therapistId())
                .orElseThrow(() -> new EntityNotFoundException("Terapeuta no encontrado"));
        if (!(user instanceof Therapist)) {
            throw new IllegalArgumentException("El usuario no es un terapeuta válido.");
        }
        Room room = roomRepository.findById(dto.roomId())
                .orElseThrow(() -> new EntityNotFoundException("Sala no encontrada"));

        Plan plan = patient.getIdPlan();
        if (plan == null || plan.getNumOfSessions() <= 0) {
            throw new IllegalArgumentException("El paciente debe tener un plan válido con sesiones asignadas.");
        }
        if (dto.firstWeekDates().size() != plan.getNumOfSessions()) {
            throw new IllegalArgumentException("Las fechas proporcionadas no coinciden con las sesiones del plan.");
        }

        for (LocalDate date : dto.firstWeekDates()) {
            validateTherapistAndRoomAvailability(dto.therapistId(), dto.roomId(), date, dto.startTime(), dto.startTime().plusMinutes(50));
        }



        Session firstCreatedSession = null;
        for (LocalDate date : dto.firstWeekDates()) {
            Session session = new Session();
            session.setSessionDate(date);
            session.setStartTime(dto.startTime());
            session.setEndTime(dto.startTime().plusMinutes(50));
            session.setPatient(patient);
            session.setTherapist((Therapist) user);
            session.setPlan(plan);
            session.setRoom(room);
            session.setTherapistPresent(false);
            session.setPatientPresent(false);

            if (firstCreatedSession == null) {
                firstCreatedSession = sessionRepository.save(session);
            } else {
                sessionRepository.save(session);
            }
        }

        return firstCreatedSession;
    }

    private void validateTherapistAndRoomAvailability(Long therapistId, Long roomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (sessionRepository.existsByTherapist_IdUserAndSessionDateAndEndTimeGreaterThanAndStartTimeLessThan(
                therapistId, date, startTime, endTime)) {
            throw new IllegalArgumentException("El terapeuta ya tiene una sesión programada en este rango de tiempo.");
        }

        if (sessionRepository.existsByRoom_IdRoomAndSessionDateAndEndTimeGreaterThanAndStartTimeLessThan(
                roomId, date, startTime, endTime)) {
            throw new IllegalArgumentException("La sala ya está ocupada en este rango de tiempo.");
        }
    }


    @Override
    public List<ListSession> getSessionsByTherapist(Long therapistId) {
        userRepository.findById(therapistId)
                .filter(user -> user instanceof Therapist)
                .orElseThrow(() -> new EntityNotFoundException("Terapeuta no encontrado"));

        return sessionRepository.findByTherapist_IdUser(therapistId)
                .stream()
                .map(session -> new ListSession(
                        session.getIdSession(),
                        session.getSessionDate(),
                        formatTime12Hour(session.getStartTime()),
                        formatTime12Hour(session.getEndTime()),
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
                        formatTime12Hour(session.getStartTime()),
                        formatTime12Hour(session.getEndTime()),
                        session.getPatient().getName(),
                        session.getTherapist().getName(),
                        session.getRoom().getName(),
                        session.isRescheduled(),
                        session.isTherapistPresent(),
                        session.isPatientPresent()
                ))
                .collect(Collectors.toList());
    }

    public List<ListSession> getSessionsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Session> sessions = sessionRepository.findBySessionDateBetween(startDate, endDate);
        return sessions.stream()
                .map(session -> new ListSession(
                        session.getIdSession(),
                        session.getSessionDate(),
                        formatTime12Hour(session.getStartTime()),
                        formatTime12Hour(session.getEndTime()),
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
        Session session = sessionRepository.findByIdSession(dto.idSession())
                .orElseThrow(() -> new EntityNotFoundException("Sesión no encontrada."));

        validateTherapistAndRoomAvailabilityUpdate(dto.therapistId(), dto.roomId(), dto.sessionDate(), dto.startTime(), dto.startTime().plusMinutes(50), dto.idSession());

        session.setSessionDate(dto.sessionDate());
        session.setStartTime(dto.startTime());
        session.setEndTime(dto.startTime().plusMinutes(50));
        session.setReason(dto.reason());
        session.setRescheduled(true);

        return sessionRepository.save(session);
    }

    private void validateTherapistAndRoomAvailabilityUpdate(Long therapistId, Long roomId, LocalDate date, LocalTime startTime, LocalTime endTime, Long sessionId) {
        if (sessionRepository.existsByTherapist_IdUserAndSessionDateAndEndTimeGreaterThanAndStartTimeLessThanAndIdSessionNot(
                therapistId, date, startTime, endTime, sessionId)) {
            throw new IllegalArgumentException("El terapeuta ya tiene una sesión programada en este rango de tiempo.");
        }

        if (sessionRepository.existsByRoom_IdRoomAndSessionDateAndEndTimeGreaterThanAndStartTimeLessThanAndIdSessionNot(
                roomId, date, startTime, endTime, sessionId)) {
            throw new IllegalArgumentException("La sala ya está ocupada en este rango de tiempo.");
        }
    }

    @Override
    public Session markPresence(MarkPresenceSession dto) {
        Session session = sessionRepository.findByIdSession(dto.sessionId())
                .orElseThrow(() -> new EntityNotFoundException("Sesión no encontrada"));

        boolean noChange = (session.isTherapistPresent() == dto.therapistPresent() &&
                session.isPatientPresent() == dto.patientPresent());
        if (noChange) {
            throw new IllegalArgumentException("No hay cambios en los datos de presencia.");
        }

        session.setTherapistPresent(dto.therapistPresent());
        session.setPatientPresent(dto.patientPresent());

        return sessionRepository.save(session);
    }

    @Override
    public void assignSessionsFromSession(Long sessionId) {
        Session initialSession = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Sesión inicial no encontrada"));

        LocalDate startDate = initialSession.getSessionDate();
        LocalTime startTime = initialSession.getStartTime();
        Patient patient = initialSession.getPatient();
        Therapist therapist = initialSession.getTherapist();
        Room room = initialSession.getRoom();
        Plan plan = initialSession.getPlan();

        if (plan == null) {
            throw new IllegalArgumentException("El paciente no tiene un plan válido asociado a la sesión");
        }

        int numOfSessionsPerWeek = plan.getNumOfSessions();
        int totalSessionsToGenerate = numOfSessionsPerWeek * 4;
        int totalSessionsGenerated = 0;

        List<DayOfWeek> selectedDays = List.of(DayOfWeek.MONDAY,DayOfWeek.TUESDAY,DayOfWeek.WEDNESDAY,DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);

        for (int week = 0; week < 4; week++) {
            LocalDate weekStartDate = startDate.plusWeeks(week);

            for (int sessionIndex = 0; sessionIndex < numOfSessionsPerWeek && totalSessionsGenerated < totalSessionsToGenerate; sessionIndex++) {
                try {
                    LocalDate sessionDate = calculateSessionDate(weekStartDate, selectedDays, sessionIndex);

                    validateTherapistAndRoomAvailability(
                            therapist.getIdUser(),
                            room.getIdRoom(),
                            sessionDate,
                            startTime,
                            startTime.plusMinutes(50)
                    );

                    Session newSession = new Session();
                    newSession.setSessionDate(sessionDate);
                    newSession.setStartTime(startTime);
                    newSession.setEndTime(startTime.plusMinutes(50));
                    newSession.setPatient(patient);
                    newSession.setPlan(plan);
                    newSession.setTherapist(therapist);
                    newSession.setRoom(room);
                    newSession.setTherapistPresent(false);
                    newSession.setPatientPresent(false);

                    sessionRepository.save(newSession);
                    totalSessionsGenerated++;
                } catch (Exception e) {
                    System.err.println("Conflicto al asignar sesión: " + e.getMessage());
                }
            }
        }

        if (totalSessionsGenerated != totalSessionsToGenerate) {
            System.err.println("Cantidad inconsistente: Se esperaban " + totalSessionsToGenerate +
                    " sesiones, pero se generaron " + totalSessionsGenerated);
        }
    }

    private LocalDate calculateSessionDate(LocalDate weekStartDate, List<DayOfWeek> selectedDays, int sessionIndex) {
        if (selectedDays == null || selectedDays.isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos un día para las sesiones.");
        }

        if (selectedDays.stream().anyMatch(day -> day == DayOfWeek.SUNDAY)) {
            throw new IllegalArgumentException("Las sesiones no pueden ser programadas los domingos.");
        }

        if (sessionIndex >= selectedDays.size()) {
            throw new IllegalArgumentException("El índice de la sesión excede los días seleccionados disponibles.");
        }

        DayOfWeek selectedDay = selectedDays.get(sessionIndex);

        LocalDate sessionDate = weekStartDate.with(selectedDay);

        if (isNotWorkingDay(sessionDate)) {
            throw new IllegalArgumentException("El día seleccionado no es válido para programar sesiones.");
        }

        return sessionDate;
    }

    @Override
    public List<ListTherapist> getAvailableTherapist(LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<User> therapists = userRepository.findByRol(Rol.THERAPIST);

        return therapists.stream()
                .filter(therapist -> isTherapistAvailable(therapist.getIdUser(), date, startTime, endTime))
                .map(therapist -> new ListTherapist(
                        therapist.getIdUser(),
                        therapist.getUsername(),
                        therapist.getName()
                )).collect(Collectors.toList());
    }

    public boolean isTherapistAvailable(Long therapistId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return !sessionRepository.existsByTherapist_IdUserAndSessionDateAndEndTimeGreaterThanAndStartTimeLessThanAndIdSessionNot(
                therapistId, date, startTime, endTime, 0L);
    }


    private boolean isInvalidTime(LocalTime startTime) {
        LocalTime endTime = startTime.plusMinutes(50);
        return !isWithinWorkingHours(startTime, endTime);
    }

    private boolean isNotWorkingDay(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    private boolean isWithinWorkingHours(LocalTime startTime, LocalTime endTime) {
        return (isMorningTime(startTime) && isMorningTime(endTime)) ||
                (isAfternoonTime(startTime) && isAfternoonTime(endTime));
    }

    private boolean isMorningTime(LocalTime time) {
        return !time.isBefore(LocalTime.of(9, 0)) && time.isBefore(LocalTime.of(13, 1));
    }

    private boolean isAfternoonTime(LocalTime time) {
        return !time.isBefore(LocalTime.of(15, 0)) && time.isBefore(LocalTime.of(19, 1));
    }

    private String formatTime12Hour(LocalTime time) {
        return time != null ? time.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a")) : null;
    }
}
