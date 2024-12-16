package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.MaterialArea.SearchInterventionArea;
import com.plenamente.sgt.domain.dto.MaterialArea.SearchMaterialArea;
import com.plenamente.sgt.domain.entity.Material;
import com.plenamente.sgt.domain.entity.MaterialArea;

import java.util.List;

public interface MaterialAreaService {
    MaterialArea createAreaForMaterial(String materialId, Long interventionAreaId);
    MaterialArea updateMaterialArea(Long id, Long interventionAreaId);
    MaterialArea assignMaterialToAreaMaterial(String materialId, Long AreaMaterialId);
    MaterialArea unassignMaterialFromAreaMaterial(Long AreaMaterialId);
    List<SearchMaterialArea> getMaterialsByAreaMaterial(String materialId);
    SearchInterventionArea getInterventionAreaByMaterialArea(Long materialAreaId);
    void deleteMaterialArea(String materialId, Long interventionAreaId);
}