package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.InterventionAreaDto.*;
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

    @PreAuthorize("hasAnyRole('THERAPIST', 'ADMIN')")
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

    // Borrado lógico (deshabilitar) área de intervención
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteInterventionArea(@PathVariable Long id) {
        interventionAreaService.deleteInterventionArea(id);
        return ResponseEntity.ok("Área de intervención con ID " + id + " deshabilitada correctamente.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<InterventionArea> updateInterventionArea(
            @PathVariable Long id,
            @RequestBody @Valid UpdateInterventionArea updateInterventionArea
    ) {
        InterventionArea updatedArea = interventionAreaService.updateInterventionArea(id, updateInterventionArea);
        return ResponseEntity.ok(updatedArea);
    }

    @GetMapping("/{id}")
    public ResponseEntity<reportInterventionArea> getInterventionArea(@PathVariable Long id) {
        InterventionArea interventionArea = interventionAreaService.getInterventionArea(id);

        reportInterventionArea dto = new reportInterventionArea(
                interventionArea.getName(),
                interventionArea.getDescription(),
                interventionArea.isEnabled()
        );

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<List<ListInterventionArea>> getInterventionArea(@PathVariable("id") String materialId) {
        List<ListInterventionArea> interventionArea = interventionAreaService.getInterventionAreaByMaterial(materialId);
        return ResponseEntity.ok(interventionArea);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/enable/{id}")
    public ResponseEntity<String> enableInterventionArea(@PathVariable Long id) {
        interventionAreaService.enableInterventionArea(id);
        return ResponseEntity.ok("Área de intervención con ID " + id + " reactivada correctamente.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/disable/{id}")
    public ResponseEntity<String> disableInterventionArea(@PathVariable Long id) {
        interventionAreaService.disableInterventionArea(id);
        return ResponseEntity.ok("Área de intervención con ID " + id + " deshabilitada correctamente.");
    }

    @GetMapping("/disabled")
    public ResponseEntity<List<DisabledInterventionArea>> getDisabledInterventionAreas() {
        List<DisabledInterventionArea> disabledAreas = interventionAreaService.getDisabledInterventionAreas();
        return ResponseEntity.ok(disabledAreas);
    }
}
