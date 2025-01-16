package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.SessionDto.*;
import com.plenamente.sgt.domain.dto.UserDto.ListTherapist;
import com.plenamente.sgt.domain.entity.Room;
import com.plenamente.sgt.domain.entity.Session;
import com.plenamente.sgt.infra.exception.ResourceNotFoundException;
import com.plenamente.sgt.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @PreAuthorize("hasAnyRole('SECRETARY', 'ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<Session> registerSession(@RequestBody RegisterSession dto) {
        Session session = sessionService.createSession(dto);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/therapist/{id}")
    public ResponseEntity<List<ListSession>> getSessionsByTherapist(@PathVariable("id") Long therapistId) {
        List<ListSession> sessions = sessionService.getSessionsByTherapist(therapistId);
        return ResponseEntity.ok(sessions);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Session> updateSession(@PathVariable("id") Long idSession,
                                                 @RequestBody UpdateSession dto) {
        UpdateSession updatedDto = new UpdateSession(
                idSession,
                dto.sessionDate(),
                dto.startTime(),
                dto.therapistId(),
                dto.roomId(),
                dto.reason()
        );

        return ResponseEntity.ok(sessionService.updateSession(updatedDto));
    }

    @GetMapping("/date")
    public ResponseEntity<List<ListSession>> getSessionsByDate(@RequestParam LocalDate date) {
        return ResponseEntity.ok(sessionService.getSessionsByDate(date));
    }

    @GetMapping("/sessions-by-month")
    public ResponseEntity<List<ListSession>> getSessionsByMonth(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        List<ListSession> sessions = sessionService.getSessionsByDateRange(startDate, endDate);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/sessions-byRoom/{roomId}")
    public ResponseEntity<List<ListSession>> getSessionsByRoom(
            @PathVariable(required = false) Long roomId,
            @RequestParam(required = false) LocalDate date) {
        try {
            if (roomId == null) {
                // Si no hay roomId, obtener todas las sesiones del día actual o la fecha específica
                return ResponseEntity.ok(sessionService.getSessionsByDate(date != null ? date : LocalDate.now()));
            }
            return ResponseEntity.ok(sessionService.getSessionsByRoom(roomId));
        } catch (ResourceNotFoundException e) {
            // En lugar de error, devolver lista vacía
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PutMapping("/presence")
    public ResponseEntity<Session> markPresence(@RequestBody PresenceDTO dto) {
        Session updatedSession = sessionService.markPresence(dto);
        return ResponseEntity.ok(updatedSession);
    }


    @PreAuthorize("hasAnyRole('SECRETARY', 'ADMIN')")
    @GetMapping("/available-therapists")
    public ResponseEntity<List<ListTherapist>> getAvailableTherapists(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sessionDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime){

        List<ListTherapist> availableTherapists = sessionService.getAvailableTherapist(sessionDate, startTime, endTime);
        return ResponseEntity.ok(availableTherapists);
    }

    @PreAuthorize("hasAnyRole('SECRETARY', 'ADMIN')")
    @GetMapping("/available-rooms")
    public ResponseEntity<List<Room>> getAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sessionDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {

        List<Room> availableRooms = sessionService.getAvailableRooms(sessionDate, startTime, endTime);
        return ResponseEntity.ok(availableRooms);
    }

    @GetMapping("/filtered")
    public ResponseEntity<List<ListSession>> getFilteredSessions(
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date,
            @RequestParam(required = false) Long therapistId,
            @RequestParam(required = false) Long roomId) {
        try {
            return ResponseEntity.ok(sessionService.getFilteredSessions(date, therapistId, roomId));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}