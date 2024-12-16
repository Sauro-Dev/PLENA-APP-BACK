package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.InterventionAreaDto.CreateAreaForIntervention;
import com.plenamente.sgt.domain.dto.InterventionAreaDto.ListInterventionArea;
import com.plenamente.sgt.domain.entity.InterventionArea;

import java.util.List;

public interface InterventionAreaService {
    InterventionArea createAreaForIntervention(String name, String description);

    List<ListInterventionArea> getAllInterventionAreas();

    InterventionArea deleteInterventionArea(Long id);

    InterventionArea updateInterventionArea(Long Id, CreateAreaForIntervention interventionArea);

    InterventionArea getInterventionArea(Long id);

    List<ListInterventionArea> getInterventionAreaByMaterial(String material);
}