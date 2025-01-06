package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.ReportDto.RegisterReport;
import com.plenamente.sgt.domain.dto.ReportDto.ReportDetailsDto;
import com.plenamente.sgt.domain.dto.ReportDto.UpdateReport;
import com.plenamente.sgt.domain.entity.Report;
import com.plenamente.sgt.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PreAuthorize("hasAnyRole('ADMIN', 'THERAPIST')")
    @PostMapping("/register")
    public ResponseEntity<Report> registerReport(@RequestBody @Valid RegisterReport report) {
        Report newReport = reportService.createReport(report);
        return new ResponseEntity<>(newReport, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'THERAPIST')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Report> updateReport(@PathVariable Long id, @RequestBody UpdateReport report) {
        Report updtReport = reportService.updateReport(id,report);
        return new ResponseEntity<>(updtReport, HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('THERAPIST', 'ADMIN')")
    @GetMapping("/select/{id}")
    public ResponseEntity<ReportDetailsDto> selectReport(@PathVariable Long id) {
        ReportDetailsDto report = reportService.findReportById(id);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('THERAPIST', 'ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<List<ReportDetailsDto>> getReportsByMedicalHistory(@RequestParam Long medicalHistoryId) {
        List<ReportDetailsDto> reports = reportService.findReportsByMedicalHistoryId(medicalHistoryId);
        return ResponseEntity.ok(reports);
    }
}
