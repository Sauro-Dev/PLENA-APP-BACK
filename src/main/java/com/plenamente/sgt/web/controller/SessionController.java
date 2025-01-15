package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.SessionDto.*;
import com.plenamente.sgt.domain.dto.UserDto.ListTherapist;
import com.plenamente.sgt.domain.entity.Plan;
import com.plenamente.sgt.domain.entity.Room;
import com.plenamente.sgt.domain.entity.Session;
import com.plenamente.sgt.infra.exception.ResourceNotFoundException;
import com.plenamente.sgt.infra.repository.PlanRepository;
import com.plenamente.sgt.service.SessionService;
import com.plenamente.sgt.service.impl.PdfGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;
    private final PdfGenerationService pdfGenerationService;
    private final PlanRepository planRepository;

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

    @PreAuthorize("hasAnyRole('THERAPIST', 'ADMIN')")
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

    @PutMapping("/presence/patient/{id}")
    public ResponseEntity<Session> markPatientPresence(@PathVariable("id") Long sessionId,
                                                       @RequestBody MarkPatientPresenceSession dto) {
        dto = new MarkPatientPresenceSession(sessionId, dto.patientPresent());
        Session updatedSession = sessionService.markPatientPresence(dto);
        return ResponseEntity.ok(updatedSession);
    }

    @PutMapping("/presence/therapist/{id}")
    public ResponseEntity<Session> markTherapistPresence(@PathVariable("id") Long sessionId,
                                                         @RequestBody MarkTherapistPresenceSession dto) {
        dto = new MarkTherapistPresenceSession(sessionId, dto.therapistPresent());
        Session updatedSession = sessionService.markTherapistPresence(dto);
        return ResponseEntity.ok(updatedSession);
    }

    @GetMapping("/available-therapists")
    public ResponseEntity<List<ListTherapist>> getAvailableTherapists(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sessionDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime){

        List<ListTherapist> availableTherapists = sessionService.getAvailableTherapist(sessionDate, startTime, endTime);
        return ResponseEntity.ok(availableTherapists);
    }

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

    @GetMapping("/report/all/pdf")
    public ResponseEntity<byte[]> getAllSessionsReportPdf() {
        List<ReportSession> reports = sessionService.getAllSessionsReport();
        byte[] pdfData = pdfGenerationService.generateAllSessionsReportPdf(reports);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=reporte_general_sesiones.pdf")
                .header("Content-Type", "application/pdf")
                .body(pdfData);
    }

    @GetMapping("/report/therapist/{id}/pdf")
    public ResponseEntity<byte[]> getSessionsReportByTherapistPdf(@PathVariable("id") Long therapistId) {
        List<ReportSession> reports = sessionService.getSessionsReportByTherapist(therapistId);

        Map<Long, Integer> planSessions = reports.stream()
                .map(ReportSession::planId)
                .distinct()
                .collect(Collectors.toMap(
                        planId -> planId,
                        planId -> planRepository.findById(planId)
                                .map(Plan::getNumOfSessions)
                                .orElse(0)
                ));

        byte[] pdfData = pdfGenerationService.generateTherapistReportPdf(reports, planSessions);

        String filename = String.format("reporte_sesiones_terapeuta_%d_%s.pdf",
                therapistId,
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .header("Content-Type", "application/pdf")
                .body(pdfData);
    }

    @GetMapping("/report/patient/{id}/pdf")
    public ResponseEntity<byte[]> getSessionsReportByPatientPdf(@PathVariable("id") Long patientId) {
        List<ReportSession> reports = sessionService.getSessionsReportByPatient(patientId);
        byte[] pdfData = pdfGenerationService.generatePatientReportPdf(reports);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=reporte_sesiones_paciente_" + patientId + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(pdfData);
    }
}