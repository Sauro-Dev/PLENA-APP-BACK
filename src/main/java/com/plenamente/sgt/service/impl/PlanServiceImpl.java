package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.PlanDto.CreatePlanDto;
import com.plenamente.sgt.domain.entity.Plan;
import com.plenamente.sgt.infra.repository.PlanRepository;
import com.plenamente.sgt.service.PlanService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private final PlanRepository planRepository;

    @Transactional
    @Override
    public Plan createPlan(CreatePlanDto createPlanDto) {
        Plan plan = new Plan();
        plan.setNumOfSessions(createPlanDto.numOfSessions());
        plan.setWeeks(4);

        return planRepository.save(plan);
    }
}
