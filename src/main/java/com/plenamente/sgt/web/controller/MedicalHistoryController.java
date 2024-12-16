package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.MedicalHistoryDto.ListMedicalHistory;
import com.plenamente.sgt.domain.dto.MedicalHistoryDto.RegisterMedicalHistory;
import com.plenamente.sgt.domain.entity.MedicalHistory;
import com.plenamente.sgt.service.MedicalHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/medicalHistory")
@RequiredArgsConstructor
public class MedicalHistoryController {
    private final MedicalHistoryService medicalHistoryService;

    @PostMapping("/register")
    public ResponseEntity<MedicalHistory> registerMedicalHistory(@RequestBody @Valid RegisterMedicalHistory medicalHistory) {
        MedicalHistory newMedicalHistory = medicalHistoryService.createMedicalHistory(medicalHistory);
        return new ResponseEntity<>(newMedicalHistory, HttpStatus.CREATED);
    }
    @GetMapping("/find/{id}")
    public ResponseEntity<List<ListMedicalHistory>> findMedicalHistoryByPatientId(@PathVariable Long id) {
        List<ListMedicalHistory> medicalHistories = medicalHistoryService.getReport_DocumentByPatientId(id);
        return ResponseEntity.ok(medicalHistories);
    }
}
