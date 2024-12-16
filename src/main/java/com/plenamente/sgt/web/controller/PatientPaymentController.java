package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.PatientPaymentDto.PatientPaymentDTO;
import com.plenamente.sgt.domain.entity.PatientPayment;
import com.plenamente.sgt.service.PatientPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patientsPayment")
@RequiredArgsConstructor
public class PatientPaymentController {

    private final PatientPaymentService patientPaymentService;

    //Registrar el pago del paciente
    @PostMapping("/register")
    public ResponseEntity<PatientPayment> registerPatientPayment(@RequestBody PatientPaymentDTO patientPaymentDTO){
        return ResponseEntity.ok(patientPaymentService.createPatientPayment(patientPaymentDTO));
    }
    //Buscar pago del paciente por el id
    @GetMapping("/select/{id}")
    public ResponseEntity<PatientPaymentDTO> getPatientPaymentById(@PathVariable Long id){
        PatientPaymentDTO patientPayment = patientPaymentService.getPatientPaymentId(id);
        return ResponseEntity.ok(patientPayment);
    }
}
