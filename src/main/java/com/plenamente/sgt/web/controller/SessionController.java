package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.SessionDto.ListSession;
import com.plenamente.sgt.domain.dto.SessionDto.MarkPresenceSession;
import com.plenamente.sgt.domain.dto.SessionDto.RegisterSession;
import com.plenamente.sgt.domain.dto.SessionDto.UpdateSession;
import com.plenamente.sgt.domain.dto.UserDto.ListTherapist;
import com.plenamente.sgt.domain.entity.Session;
import com.plenamente.sgt.domain.entity.User;
import com.plenamente.sgt.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @PostMapping("/register" )
    public ResponseEntity<Session> registerSession(@RequestBody RegisterSession dto) {
        Session session = sessionService.createSession(dto);
        sessionService.assignSessionsFromSession(session.getIdSession());
        return ResponseEntity.ok(session);
    }

    @GetMapping("/therapist/{id}")
    public ResponseEntity<List<ListSession>> getSessionsByTherapist(@PathVariable("id") Long therapistId) {
        List<ListSession> sessions = sessionService.getSessionsByTherapist(therapistId);
        return ResponseEntity.ok(sessions);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Session> updateSession(@PathVariable("id") Long idSession,
                                                 @RequestBody UpdateSession dto) {
        UpdateSession updatedDto = new UpdateSession(
                idSession,
                dto.sessionDate(),
                dto.startTime(),
                dto.endTime(),
                dto.reason()
        );

        return ResponseEntity.ok(sessionService.updateSession(updatedDto));
    }

    @GetMapping("/date")
    public ResponseEntity<List<ListSession>> getSessionsByDate(@RequestParam LocalDate date) {
        return ResponseEntity.ok(sessionService.getSessionsByDate(date));
    }

    @PutMapping("/presence/{id}")
    public ResponseEntity<Session> markPresence(@PathVariable("id") Long sessionId,
                                                @RequestBody MarkPresenceSession dto) {
        dto = new MarkPresenceSession(sessionId, dto.therapistPresent(), dto.patientPresent());
        Session updatedSession = sessionService.markPresence(dto);
        return ResponseEntity.ok(updatedSession);
    }

    @PostMapping("/assign-from-session/{sessionId}")
    public ResponseEntity<String> assignSessionsFromSession(@PathVariable Long sessionId) {
        try {
            sessionService.assignSessionsFromSession(sessionId);
            return ResponseEntity.ok("Sesiones asignadas correctamente a partir de la sesión con ID " + sessionId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error al asignar sesiones: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    @GetMapping("/available-therapists")
    public ResponseEntity<List<ListTherapist>> getAvailableTherapists(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sessionDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {

        List<ListTherapist> availableTherapists = sessionService.getAvailableTherapist(sessionDate, startTime, endTime);
        return ResponseEntity.ok(availableTherapists);
    }
}