package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.SessionDto.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @Override
    public List<ListSession> getFilteredSessions(LocalDate date, Long therapistId, Long roomId) {
        List<Session> sessions;

        if (date != null) {
            sessions = sessionRepository.findBySessionDate(date);
        } else {
            sessions = sessionRepository.findBySessionDateGreaterThanEqual(LocalDate.now());
        }

        Stream<Session> sessionStream = sessions.stream();

        if (therapistId != null && !therapistId.toString().isEmpty()) {
            sessionStream = sessionStream.filter(s -> s.getTherapist().getIdUser().equals(therapistId));
        }

        if (roomId != null) {
            sessionStream = sessionStream.filter(s -> s.getRoom().getIdRoom().equals(roomId));
        }

        return sessionStream.map(this::mapToListSession).collect(Collectors.toList());
    }

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

        if (!user.isTherapist()) {
            throw new IllegalArgumentException("El usuario no es un terapeuta válido.");
        }

        Room room = roomRepository.findById(dto.roomId())
                .orElseThrow(() -> new EntityNotFoundException("Sala no encontrada"));

        Plan plan = patient.getIdPlan();
        if (plan == null || plan.getNumOfSessions() == null || plan.getWeeks() == null) {
            throw new IllegalArgumentException("El paciente debe tener un plan válido con sesiones asignadas y una duración de semanas definida.");
        }

        if (plan.getNumOfSessions() <= 0 || plan.getWeeks() <= 0) {
            throw new IllegalArgumentException("El plan del paciente debe tener un número válido de sesiones por semana y una duración positiva en semanas.");
        }

        for (LocalDate date : dto.firstWeekDates()) {
            validateTherapistAndRoomAvailability(dto.therapistId(), dto.roomId(), date, dto.startTime(), dto.startTime().plusMinutes(50));
        }

        int numOfWeeks = plan.getWeeks();
        int sessionsPerWeek = plan.getNumOfSessions();
        List<LocalDate> allDates = generateSessionDates(dto.firstWeekDates(), numOfWeeks, sessionsPerWeek);

        Session firstCreatedSession = null;
        for (LocalDate date : allDates) {
            if (sessionRepository.existsByTherapist_IdUserAndSessionDateAndEndTimeGreaterThanAndStartTimeLessThan(
                    dto.therapistId(), date, dto.startTime(), dto.startTime().plusMinutes(50))) {
                throw new IllegalArgumentException("Ya existe una sesión programada para el terapeuta en esta fecha y hora.");
            }

            if (sessionRepository.existsByRoom_IdRoomAndSessionDateAndEndTimeGreaterThanAndStartTimeLessThan(
                    dto.roomId(), date, dto.startTime(), dto.startTime().plusMinutes(50))) {
                throw new IllegalArgumentException("La sala ya está ocupada en esta fecha y hora.");
            }

            Session session = new Session();
            session.setSessionDate(date);
            session.setStartTime(dto.startTime());
            session.setEndTime(dto.startTime().plusMinutes(50));
            session.setPatient(patient);
            session.setTherapist(user);
            session.setPlan(plan);
            session.setRoom(room);
            session.setTherapistPresent(false);
            session.setPatientPresent(false);
            session.setRenewPlan(dto.renewPlan());

            if (firstCreatedSession == null) {
                firstCreatedSession = sessionRepository.save(session);
            } else {
                sessionRepository.save(session);
            }
        }

        return firstCreatedSession;
    }

    private List<LocalDate> generateSessionDates(List<LocalDate> firstWeekDates, int numOfWeeks, int sessionsPerWeek) {
        if (firstWeekDates.size() != sessionsPerWeek) {
            throw new IllegalArgumentException("El número de fechas proporcionadas no coincide con las sesiones semanales del plan.");
        }

        List<LocalDate> allDates = new ArrayList<>();
        for (int week = 0; week < numOfWeeks; week++) {
            for (LocalDate date : firstWeekDates) {
                allDates.add(date.plusWeeks(week));
            }
        }
        return allDates;
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
                .filter(User::isTherapist)
                .orElseThrow(() -> new EntityNotFoundException("Terapeuta no encontrado"));

        return sessionRepository.findByTherapist_IdUser(therapistId)
                .stream()
                .map(this::mapToListSession)
                .collect(Collectors.toList());
    }


    @Override
    public List<ListSession> getSessionsByDate(LocalDate date) {
        List<Session> sessions = sessionRepository.findBySessionDate(date);

        if (sessions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron sesiones para la fecha " + date);
        }

        return sessions.stream()
                .map(this::mapToListSession)
                .collect(Collectors.toList());
    }

    public List<ListSession> getSessionsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Session> sessions = sessionRepository.findBySessionDateBetween(startDate, endDate);
        return sessions.stream()
                .map(this::mapToListSession)
                .collect(Collectors.toList());
    }

    @Override
    public List<ListSession> getSessionsByRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Sala no encontrada."));

        List<Session> sessions = sessionRepository.findByRoom_IdRoom(roomId);

        if (sessions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron sesiones para la sala " + room.getName());
        }

        return sessions.stream()
                .map(this::mapToListSession)
                .collect(Collectors.toList());
    }

    private ListSession mapToListSession(Session session) {
        return new ListSession(
                session.getIdSession(),
                session.getTherapist().getIdUser(),
                session.getRoom().getIdRoom(),
                session.getSessionDate(),
                formatTime12Hour(session.getStartTime()),
                formatTime12Hour(session.getEndTime()),
                session.getPatient().getName(),
                session.getTherapist().getName(),
                session.getRoom().getName(),
                session.isRescheduled(),
                session.isTherapistPresent(),
                session.isPatientPresent(),
                session.getReason()
        );
    }

    @Override
    public Session updateSession(UpdateSession dto) {
        Session session = sessionRepository.findByIdSession(dto.idSession())
                .orElseThrow(() -> new EntityNotFoundException("Sesión no encontrada."));

        if (isNotWorkingDay(dto.sessionDate())) {
            throw new IllegalArgumentException("La fecha proporcionada es un día no laborable (domingo).");
        }

        if (isInvalidTime(dto.startTime())) {
            throw new IllegalArgumentException("El horario proporcionado está fuera de las horas laborales permitidas.");
        }

        validateTherapistAndRoomAvailabilityUpdate(dto.therapistId(), dto.roomId(), dto.sessionDate(), dto.startTime(), dto.startTime().plusMinutes(50), dto.idSession());

        session.setSessionDate(dto.sessionDate());
        session.setStartTime(dto.startTime());
        session.setEndTime(dto.startTime().plusMinutes(50));
        session.setTherapist(userRepository.findById(dto.therapistId())
                .filter(User::isTherapist)
                .orElseThrow(() -> new EntityNotFoundException("Terapeuta no encontrado.")));
        session.setRoom(roomRepository.findById(dto.roomId())
                .orElseThrow(() -> new EntityNotFoundException("Sala no encontrada.")));
        session.setReason(dto.reason());
        session.setRescheduled(true);
        session.setTherapistPresent(false);
        session.setPatientPresent(false);

        return sessionRepository.save(session);
    }

    private void validateTherapistAndRoomAvailabilityUpdate(Long therapistId, Long roomId, LocalDate date, LocalTime startTime, LocalTime endTime, Long sessionId) {
        if (sessionRepository.existsByRoom_IdRoomAndSessionDateAndEndTimeGreaterThanAndStartTimeLessThanAndIdSessionNot(
                roomId, date, startTime, endTime, sessionId)) {
            throw new IllegalArgumentException("La sala ya está ocupada por otra sesión en este rango de tiempo.");
        }

        if (sessionRepository.existsByTherapist_IdUserAndSessionDateAndEndTimeGreaterThanAndStartTimeLessThanAndIdSessionNot(
                therapistId, date, startTime, endTime, sessionId)) {
            throw new IllegalArgumentException("El terapeuta ya tiene una sesión programada en este rango de tiempo.");
        }
    }

    @Override
    public Session markPatientPresence(MarkPatientPresenceSession dto) {
        Session session = sessionRepository.findByIdSession(dto.sessionId())
                .orElseThrow(() -> new EntityNotFoundException("Sesión no encontrada"));

        if (session.isPatientPresent() == dto.patientPresent()) {
            throw new IllegalArgumentException("No hay cambios en la asistencia del paciente.");
        }

        session.setPatientPresent(dto.patientPresent());
        return sessionRepository.save(session);
    }

    @Override
    public Session markTherapistPresence(MarkTherapistPresenceSession dto) {
        Session session = sessionRepository.findByIdSession(dto.sessionId())
                .orElseThrow(() -> new EntityNotFoundException("Sesión no encontrada"));

        if (session.isTherapistPresent() == dto.therapistPresent()) {
            throw new IllegalArgumentException("No hay cambios en la asistencia del terapeuta.");
        }

        session.setTherapistPresent(dto.therapistPresent());
        return sessionRepository.save(session);
    }


    @Override
    public List<ListTherapist> getAvailableTherapist(LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<User> therapists = userRepository.findByIsTherapistTrue();

        return therapists.stream()
                .filter(therapist -> isTherapistAvailable(therapist.getIdUser(), date, startTime, endTime))
                .map(therapist -> new ListTherapist(
                        therapist.getIdUser(),
                        therapist.getUsername(),
                        therapist.getName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Room> allRooms = roomRepository.findAll();

        List<Room> occupiedRooms = sessionRepository
                .findBySessionDateAndStartTimeLessThanAndEndTimeGreaterThan(date, endTime, startTime)
                .stream()
                .map(Session::getRoom)
                .toList();

        return allRooms.stream()
                .filter(room -> !occupiedRooms.contains(room))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportSession> getAllSessionsReport() {
        return sessionRepository.findAll()
                .stream()
                .map(session -> new ReportSession(
                        session.getIdSession(),
                        session.getSessionDate(),
                        session.getStartTime(),
                        session.getEndTime(),
                        session.isTherapistPresent(),
                        session.isPatientPresent(),
                        session.getTherapist().getName(),
                        session.getPatient().getName(),
                        session.getRoom().getName(),
                        session.getRenewPlan(),
                        session.getReason(),
                        session.getPatient().getIdPatient(),
                        session.getPlan().getIdPlan(),
                        session.getRoom().getIdRoom(),
                        session.getTherapist().getIdUser(),
                        session.isRescheduled()
                )).collect(Collectors.toList());
    }

    @Override
    public List<ReportSession> getSessionsReportByTherapist(Long therapistId) {
        userRepository.findById(therapistId)
                .filter(User::isTherapist)
                .orElseThrow(() -> new ResourceNotFoundException("Terapeuta no encontrado"));

        return sessionRepository.findByTherapist_IdUser(therapistId)
                .stream()
                .map(session -> new ReportSession(
                        session.getIdSession(),
                        session.getSessionDate(),
                        session.getStartTime(),
                        session.getEndTime(),
                        session.isTherapistPresent(),
                        session.isPatientPresent(),
                        session.getTherapist().getName(),
                        session.getPatient().getName(),
                        session.getRoom().getName(),
                        session.getRenewPlan(),
                        session.getReason(),
                        session.getPatient().getIdPatient(),
                        session.getPlan().getIdPlan(),
                        session.getRoom().getIdRoom(),
                        session.getTherapist().getIdUser(),
                        session.isRescheduled()
                )).collect(Collectors.toList());
    }

    @Override
    public List<ReportSession> getSessionsReportByPatient(Long patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        return sessionRepository.findByPatient_IdPatient(patientId)
                .stream()
                .map(session -> new ReportSession(
                        session.getIdSession(),
                        session.getSessionDate(),
                        session.getStartTime(),
                        session.getEndTime(),
                        session.isTherapistPresent(),
                        session.isPatientPresent(),
                        session.getTherapist().getName(),
                        session.getPatient().getName(),
                        session.getRoom().getName(),
                        session.getRenewPlan(),
                        session.getReason(),
                        session.getPatient().getIdPatient(),
                        session.getPlan().getIdPlan(),
                        session.getRoom().getIdRoom(),
                        session.getTherapist().getIdUser(),
                        session.isRescheduled()
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
