package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.MaterialDto.ListMaterial;
import com.plenamente.sgt.domain.dto.MaterialDto.RegisterMaterial;
import com.plenamente.sgt.domain.entity.Material;
import com.plenamente.sgt.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping("/register")
    public ResponseEntity<Material> registerMaterial(@RequestBody RegisterMaterial dto) {
        Material newMaterial = materialService.registerMaterial(dto);
        return new ResponseEntity<>(newMaterial, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RegisterMaterial>> getAllMaterials() {
        List<RegisterMaterial> materials = materialService.getAllMaterials();
        return new ResponseEntity<>(materials, HttpStatus.OK);
    }

    @GetMapping("/select/{id}")
    public ResponseEntity<ListMaterial> getMaterialById(@PathVariable String id) {
        ListMaterial material = materialService.getMaterialById(id);
        return new ResponseEntity<>(material, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Material> updateMaterial(
            @PathVariable String id,
            @RequestBody RegisterMaterial updatedMaterial) {
        Material updated = materialService.updateMaterial(id, updatedMaterial);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PostMapping("/{materialId}/assign/{roomId}")
    public ResponseEntity<Material> assignMaterialToRoom(
            @PathVariable String materialId,
            @PathVariable Long roomId) {
        Material assignedMaterial = materialService.assignMaterialToRoom(materialId, roomId);
        return new ResponseEntity<>(assignedMaterial, HttpStatus.OK);
    }

    @PostMapping("/{materialId}/unassign")
    public ResponseEntity<Material> unassignMaterialFromRoom(@PathVariable String materialId) {
        Material unassignedMaterial = materialService.unassignMaterialFromRoom(materialId);
        return new ResponseEntity<>(unassignedMaterial, HttpStatus.OK);
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<RegisterMaterial>> getUnassignedMaterials() {
        List<RegisterMaterial> unassignedMaterials = materialService.getUnassignedMaterials();
        return new ResponseEntity<>(unassignedMaterials, HttpStatus.OK);
    }
    @DeleteMapping("/{materialId}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable String materialId) {
        materialService.deleteMaterial(materialId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
