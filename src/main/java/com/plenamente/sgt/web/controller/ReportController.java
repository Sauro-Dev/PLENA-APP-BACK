package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.MedicalHistoryDto.RegisterMedicalHistory;
import com.plenamente.sgt.domain.dto.ReportDto.RegisterReport;
import com.plenamente.sgt.domain.dto.ReportDto.UpdateReport;
import com.plenamente.sgt.domain.entity.MedicalHistory;
import com.plenamente.sgt.domain.entity.Report;
import com.plenamente.sgt.service.MedicalHistoryService;
import com.plenamente.sgt.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping("/register")
    public ResponseEntity<Report> registerReport(@RequestBody @Valid RegisterReport report) {
        Report newReport = reportService.createReport(report);
        return new ResponseEntity<>(newReport, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Report> updateReport(@PathVariable Long id, @RequestBody UpdateReport report) {
        Report updtReport = reportService.updateReport(id,report);
        return new ResponseEntity<>(updtReport, HttpStatus.OK);
    }
}
