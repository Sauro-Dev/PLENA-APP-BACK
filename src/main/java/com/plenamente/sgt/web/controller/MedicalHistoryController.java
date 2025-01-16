package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.MedicalHistoryDto.ListMedicalHistory;
import com.plenamente.sgt.service.MedicalHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/medical-history")
public class MedicalHistoryController {
    private final MedicalHistoryService medicalHistoryService;

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ListMedicalHistory>> getMedicalHistoryByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalHistoryService.getReport_DocumentByPatientId(patientId));
    }
}