package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.PatientDto.RegisterPatient;
import com.plenamente.sgt.domain.dto.PatientDto.UpdatePatient;
import com.plenamente.sgt.domain.dto.PatientDto.ListPatient;
import com.plenamente.sgt.domain.entity.Patient;
import com.plenamente.sgt.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    // Endpoint para registrar un paciente
    @PostMapping("/register")
    public ResponseEntity<Patient> registerPatient(@RequestBody RegisterPatient registerPatient) {
        return ResponseEntity.ok(patientService.createPatient(registerPatient));
    }

    // Endpoint para listar todos los pacientes
    @GetMapping("/all")
    public ResponseEntity<List<ListPatient>> getAllPatients() {
        List<ListPatient> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    // Endpoint para obtener un paciente por ID
    @GetMapping("/select/{id}")
    public ResponseEntity<ListPatient> getPatientById(@PathVariable Long id) {
        ListPatient patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    // Endpoint para actualizar un paciente
    @PutMapping("select/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody UpdatePatient updatePatient) {
        Patient updatedPatient = patientService.updatePatient(id, updatePatient);
        return ResponseEntity.ok(updatedPatient);
    }

    // Endpoint para filtrar pacientes por nombre
    @GetMapping("/filter/name")
    public ResponseEntity<List<ListPatient>> filterPatientsByName(@RequestParam String name) {
        List<ListPatient> filteredPatients = patientService.filterPatientsByName(name);
        return ResponseEntity.ok(filteredPatients);
    }

    // Endpoint para filtrar pacientes por plan
    @GetMapping("/filter/plan")
    public ResponseEntity<List<ListPatient>> filterPatientsByPlan(@RequestParam Long planId) {
        List<ListPatient> filteredPatients = patientService.filterPatientsByPlan(planId);
        return ResponseEntity.ok(filteredPatients);
    }

    // Endpoint para ordenar pacientes por nombre
    @GetMapping("/order")
    public ResponseEntity<List<ListPatient>> orderPatientsByName(@RequestParam String order) {
        List<ListPatient> orderedPatients = patientService.orderPatientsByName(order);
        return ResponseEntity.ok(orderedPatients);
    }
}
