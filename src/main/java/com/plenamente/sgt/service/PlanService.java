package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.PlanDto.CreatePlanDto;
import com.plenamente.sgt.domain.entity.Plan;
import jakarta.transaction.Transactional;

public interface PlanService {
    @Transactional
    Plan createPlan(CreatePlanDto createPlanDto);
}
