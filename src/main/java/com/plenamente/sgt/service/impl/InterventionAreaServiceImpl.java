package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.InterventionAreaDto.CreateAreaForIntervention;
import com.plenamente.sgt.domain.dto.InterventionAreaDto.DisabledInterventionArea;
import com.plenamente.sgt.domain.dto.InterventionAreaDto.ListInterventionArea;
import com.plenamente.sgt.domain.entity.InterventionArea;
import com.plenamente.sgt.domain.entity.Material;
import com.plenamente.sgt.infra.repository.InterventionAreaRepository;
import com.plenamente.sgt.infra.repository.MaterialAreaRepository;
import com.plenamente.sgt.infra.repository.MaterialRepository;
import com.plenamente.sgt.mapper.InterventionAreaMapper;
import com.plenamente.sgt.service.InterventionAreaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class InterventionAreaServiceImpl implements InterventionAreaService {

    private final InterventionAreaRepository interventionAreaRepository;
    private final InterventionAreaMapper interventionAreaMapper;
    private final MaterialRepository materialRepository;
    private final MaterialAreaRepository materialAreaRepository;

    @Override
    public InterventionArea createAreaForIntervention(String name, String description) {
        InterventionArea interventionArea = new InterventionArea();
        interventionArea.setName(name);
        interventionArea.setDescription(description);
        interventionArea.setEnabled(true);
        return interventionAreaRepository.save(interventionArea);
    }

    @Override
    public List<ListInterventionArea> getAllInterventionAreas() {
        List<InterventionArea> interventionAreas = interventionAreaRepository.findByEnabledTrue();
        return interventionAreas.stream()
                .map(interventionAreaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InterventionArea deleteInterventionArea(Long id){
        InterventionArea existingArea = interventionAreaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Intervention Area no encontrado con id: " + id));

        existingArea.setEnabled(false);
        return interventionAreaRepository.save(existingArea);
    }

    @Override
    public InterventionArea updateInterventionArea(Long id, @RequestBody CreateAreaForIntervention interventionArea) {
        InterventionArea existingInterventionArea = interventionAreaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Área de intervención no encontrada con id: " + id));
        existingInterventionArea.setName(interventionArea.name());
        existingInterventionArea.setDescription(interventionArea.description());
        return interventionAreaRepository.save(existingInterventionArea);
    }

    @Override
    public InterventionArea getInterventionArea(Long id) {
        InterventionArea existingInterventionArea = interventionAreaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La área de intervención no fue encontrada" + id));
        return existingInterventionArea;
    }

    public List<ListInterventionArea> getInterventionAreaByMaterial(String materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new EntityNotFoundException("Material no encontrado con id: " + materialId));

        return materialAreaRepository.findByMaterial(material).stream()
                .map(materialArea -> {
                    InterventionArea interventionArea = materialArea.getInterventionArea();
                    return new ListInterventionArea(
                            interventionArea.getIdInterventionArea(),
                            interventionArea.getName(),
                            interventionArea.getDescription()
                    );
                })
                .toList();
    }

    // Método para habilitar un área de intervención
    @Override
    public void enableInterventionArea(Long id) {
        InterventionArea area = interventionAreaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Área de intervención no encontrada con id: " + id));

        if (area.isEnabled()) {
            throw new IllegalArgumentException("El área de intervención ya está habilitada.");
        }

        area.setEnabled(true);
        interventionAreaRepository.save(area);
    }

    // Método para deshabilitar un área de intervención (si se requiere directamente)
    public void disableInterventionArea(Long id) {
        InterventionArea area = interventionAreaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Área de intervención no encontrada con id: " + id));

        if (!area.isEnabled()) {
            throw new IllegalArgumentException("El área de intervención ya está deshabilitada.");
        }

        area.setEnabled(false);
        interventionAreaRepository.save(area);
    }

    @Override
    public List<DisabledInterventionArea> getDisabledInterventionAreas() {
        List<InterventionArea> disabledAreas = interventionAreaRepository.findByEnabledFalse();
        return disabledAreas.stream()
                .map(area -> new DisabledInterventionArea(
                        area.getIdInterventionArea(),
                        area.getName(),
                        area.getDescription()
                ))
                .collect(Collectors.toList());
    }
}
