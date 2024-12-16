package com.plenamente.sgt.infra.repository;

import com.plenamente.sgt.domain.entity.InterventionArea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InterventionAreaRepository extends JpaRepository<InterventionArea, Long> {
    Optional<InterventionArea> findByName(String name);
    @Override
    Optional<InterventionArea> findById(Long id);
}