
package com.plenamente.sgt.infra.repository;

import com.plenamente.sgt.domain.entity.InterventionArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterventionAreaRepository extends JpaRepository<InterventionArea, Long> {

    // Buscar por nombre
    Optional<InterventionArea> findByName(String name);

    // Sobrescribir el método findById
    @Override
    Optional<InterventionArea> findById(Long id);

    // Filtrar áreas de intervención activas (enabled = true)
    List<InterventionArea> findByEnabledTrue();

    List<InterventionArea> findByEnabledFalse();
}