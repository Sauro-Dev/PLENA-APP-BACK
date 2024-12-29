package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.infra.repository.PlanRepository;
import com.plenamente.sgt.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private final PlanRepository planRepository;
}
