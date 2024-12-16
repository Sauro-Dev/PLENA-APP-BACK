package com.plenamente.sgt.infra.repository;

import com.plenamente.sgt.domain.entity.InterventionArea;
import com.plenamente.sgt.domain.entity.Material;
import com.plenamente.sgt.domain.entity.MaterialArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaterialAreaRepository extends JpaRepository<MaterialArea, Long> {
    List<MaterialArea> findByMaterial(Material material);
    void deleteByMaterial_IdMaterial(String materialId);
    Optional<MaterialArea> findByMaterial_IdMaterialAndInterventionArea_IdInterventionArea(String  materialId, Long interventionAreaId);
    MaterialArea findByMaterialAndInterventionArea(Material material, InterventionArea interventionArea);
}
