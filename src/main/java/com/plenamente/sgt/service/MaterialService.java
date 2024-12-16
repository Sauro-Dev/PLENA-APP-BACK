package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.MaterialDto.ListMaterial;
import com.plenamente.sgt.domain.dto.MaterialDto.RegisterMaterial;
import com.plenamente.sgt.domain.entity.Material;

import java.util.List;

public interface MaterialService {
    Material registerMaterial(RegisterMaterial dto);
    List<RegisterMaterial> getAllMaterials();
    ListMaterial getMaterialById(String id);
    Material updateMaterial(String id, RegisterMaterial updatedMaterial);
    String generateNextMaterialId();
    String incrementAlphaPart(String alphaPart);
    Material assignMaterialToRoom(String materialId, Long roomId);
    Material unassignMaterialFromRoom(String materialId);
    List<RegisterMaterial> getUnassignedMaterials();
    void deleteMaterial(String materialId);
}