package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.InterventionAreaDto.CreateAreaForIntervention;
import com.plenamente.sgt.domain.dto.InterventionAreaDto.ListInterventionArea;
import com.plenamente.sgt.domain.dto.InterventionAreaDto.reportInterventionArea;
import com.plenamente.sgt.domain.entity.InterventionArea;
import com.plenamente.sgt.service.InterventionAreaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/intervention-areas")
@CrossOrigin("")
@RequiredArgsConstructor
public class InterventionAreaController {

    private final InterventionAreaService interventionAreaService;

    @PostMapping("/register")
    public ResponseEntity<InterventionArea> createInterventionArea(@RequestBody @Valid CreateAreaForIntervention dto) {
        InterventionArea interventionArea = interventionAreaService.createAreaForIntervention(dto.name(), dto.description());
        return new ResponseEntity<>(interventionArea, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ListInterventionArea>> getAllInterventionAreas() {
        List<ListInterventionArea> areas = interventionAreaService.getAllInterventionAreas();
        return new ResponseEntity<>(areas, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<InterventionArea> deleteInterventionArea(@PathVariable Long id) {
        interventionAreaService.deleteInterventionArea(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<InterventionArea> updateInterventionArea(@PathVariable Long id, @RequestBody CreateAreaForIntervention createInterventionArea){
        interventionAreaService.updateInterventionArea(id,createInterventionArea);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<reportInterventionArea> getInterventionArea(@PathVariable Long id) {
        InterventionArea interventionArea = interventionAreaService.getInterventionArea(id);

        // Crear el DTO utilizando el constructor vacío y setters
        reportInterventionArea dto = new reportInterventionArea();
        dto.setName(interventionArea.getName());
        dto.setDescription(interventionArea.getDescription());

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
    @GetMapping("/find/{id}")
    public ResponseEntity<List<ListInterventionArea>> getInterventionArea(@PathVariable("id") String materialId) {
        List<ListInterventionArea> interventionArea = interventionAreaService.getInterventionAreaByMaterial(materialId);
        return ResponseEntity.ok(interventionArea);
    }
}