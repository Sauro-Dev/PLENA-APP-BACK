package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.MaterialArea.SearchInterventionArea;
import com.plenamente.sgt.domain.dto.MaterialArea.SearchMaterialArea;
import com.plenamente.sgt.domain.entity.InterventionArea;
import com.plenamente.sgt.domain.entity.Material;
import com.plenamente.sgt.domain.entity.MaterialArea;
import com.plenamente.sgt.infra.exception.ResourceNotFoundException;
import com.plenamente.sgt.infra.repository.InterventionAreaRepository;
import com.plenamente.sgt.infra.repository.MaterialAreaRepository;
import com.plenamente.sgt.infra.repository.MaterialRepository;
import com.plenamente.sgt.service.MaterialAreaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MaterialAreaServiceImpl implements MaterialAreaService {

    private final MaterialAreaRepository materialAreaRepository;
    private final InterventionAreaRepository interventionAreaRepository;
    private final MaterialRepository materialRepository;

    @Override
    public MaterialArea createAreaForMaterial(String materialId, Long interventionAreaId) {
        // Buscar el Material por su ID
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material no encontrado con ID: " + materialId));

        // Buscar el InterventionArea por su ID
        InterventionArea interventionArea = interventionAreaRepository.findById(interventionAreaId)
                .orElseThrow(() -> new ResourceNotFoundException("Área de intervención no encontrada con ID: " + interventionAreaId));

        // Verificar si ya existe una relación entre el Material y el InterventionArea
        MaterialArea existingMaterialArea = materialAreaRepository.findByMaterialAndInterventionArea(material, interventionArea);
        if (existingMaterialArea != null) {
            throw new IllegalStateException("El material ya está asignado a esta área de intervención.");
        }

        // Crear el objeto MaterialArea
        MaterialArea materialArea = new MaterialArea();
        materialArea.setMaterial(material);  // Asignar el material
        materialArea.setInterventionArea(interventionArea);  // Asignar el área de intervención

        // Guardar el MaterialArea
        return materialAreaRepository.save(materialArea);
    }

    @Override
    public MaterialArea updateMaterialArea(Long id, Long interventionAreaId) {
        MaterialArea materialArea = materialAreaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Área de material no encontrada con id: " + id));

        InterventionArea interventionArea = interventionAreaRepository.findById(interventionAreaId)
                .orElseThrow(() -> new ResourceNotFoundException("Área de intervención no encontrada con nombre: " + interventionAreaId));

        materialArea.setInterventionArea(interventionArea);
        return materialAreaRepository.save(materialArea);
    }

    @Override
    public MaterialArea assignMaterialToAreaMaterial(String materialId, Long AreaMaterialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new EntityNotFoundException("Material no encontrado con id: " + materialId));

        MaterialArea materialArea = materialAreaRepository.findById(AreaMaterialId)
                .orElseThrow(() -> new EntityNotFoundException("Ambiente no encontrado con id: " + AreaMaterialId));

        materialArea.setMaterial(material);
        return materialAreaRepository.save(materialArea);
    }

    @Override
    public MaterialArea unassignMaterialFromAreaMaterial(Long AreaMaterialId) {
        MaterialArea materialArea = materialAreaRepository.findById(AreaMaterialId)
                .orElseThrow(() -> new EntityNotFoundException("Ambiente no encontrado con id: " + AreaMaterialId));

        materialArea.setMaterial(null);
        return materialAreaRepository.save(materialArea);
    }

    @Override
    public List<SearchMaterialArea> getMaterialsByAreaMaterial(String materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new EntityNotFoundException("Material no encontrado con id: " + materialId));

        return materialAreaRepository.findByMaterial(material).stream()
                .map(materialArea -> new SearchMaterialArea(materialArea.getIdMaterialArea()))
                .toList();
    }
    @Override
    public SearchInterventionArea getInterventionAreaByMaterialArea(Long materialAreaId) {
        MaterialArea materialArea = materialAreaRepository.findById(materialAreaId)
                .orElseThrow(() -> new EntityNotFoundException("MaterialArea no encontrado con id: " + materialAreaId));
        return new SearchInterventionArea(materialArea.getInterventionArea().getIdInterventionArea());
    }


    @Override
    public void deleteMaterialArea(String  materialId, Long interventionAreaId) {
        // Buscar el MaterialArea por materialId e interventionAreaId
        MaterialArea materialArea = materialAreaRepository.findByMaterial_IdMaterialAndInterventionArea_IdInterventionArea(materialId, interventionAreaId)
                .orElseThrow(() -> new EntityNotFoundException("MaterialArea no encontrado con materialId: " + materialId + " y interventionAreaId: " + interventionAreaId));

        // Eliminar el MaterialArea de la base de datos
        materialAreaRepository.delete(materialArea);
    }
}