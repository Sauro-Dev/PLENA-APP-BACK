package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.InterventionAreaDto.DisabledInterventionArea;
import com.plenamente.sgt.domain.dto.InterventionAreaDto.ListInterventionArea;
import com.plenamente.sgt.domain.dto.InterventionAreaDto.UpdateInterventionArea;
import com.plenamente.sgt.domain.entity.InterventionArea;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface InterventionAreaService {
    InterventionArea createAreaForIntervention(String name, String description);

    InterventionArea updateInterventionArea(Long id, @RequestBody UpdateInterventionArea updateInterventionArea);

    List<ListInterventionArea> getAllInterventionAreas();

    InterventionArea deleteInterventionArea(Long id);

    InterventionArea getInterventionArea(Long id);

    List<ListInterventionArea> getInterventionAreaByMaterial(String material);

    void enableInterventionArea(Long id);

    void disableInterventionArea(Long id);

    List<DisabledInterventionArea> getDisabledInterventionAreas();
}
