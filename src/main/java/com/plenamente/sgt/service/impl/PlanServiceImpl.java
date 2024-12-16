package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.PlanDto.PlanDto;
import com.plenamente.sgt.domain.entity.Plan;
import com.plenamente.sgt.infra.repository.PlanRepository;
import com.plenamente.sgt.service.PlanService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private final PlanRepository planRepository;
}
